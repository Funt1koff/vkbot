package com.funtikov.service.impl;

import com.funtikov.dto.keyboard.ButtonDto;
import com.funtikov.dto.keyboard.ButtonResponseDto;
import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.dto.keyboard.MediaDto;
import com.funtikov.dto.keyboard.response.KeyboardPageRequestResponseDto;
import com.funtikov.dto.media.UploadMedia;
import com.funtikov.dto.media.UploadMediaResult;
import com.funtikov.entity.keyboard.Button;
import com.funtikov.entity.keyboard.ButtonResponse;
import com.funtikov.entity.keyboard.KeyboardPage;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.mapper.KeyboardMapper;
import com.funtikov.repository.ButtonRepository;
import com.funtikov.repository.ButtonResponseRepository;
import com.funtikov.repository.KeyboardPageRepository;
import com.funtikov.repository.MediaRepository;
import com.funtikov.service.KeyboardPageService;
import com.funtikov.service.UploadPhotoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeyboardPageServiceImpl implements KeyboardPageService {

    private final KeyboardPageRepository keyboardPageRepository;
    private final ButtonRepository buttonRepository;
    private final ButtonResponseRepository buttonResponseRepository;
    private final MediaRepository mediaRepository;
    private final UploadPhotoService uploadPhotoService;
    private final KeyboardMapper keyboardMapper;

    public KeyboardPageServiceImpl(KeyboardPageRepository keyboardPageRepository,
                                   ButtonRepository buttonRepository,
                                   ButtonResponseRepository buttonResponseRepository,
                                   MediaRepository mediaRepository,
                                   UploadPhotoService uploadPhotoService,
                                   KeyboardMapper keyboardMapper) {
        this.keyboardPageRepository = keyboardPageRepository;
        this.buttonRepository = buttonRepository;
        this.buttonResponseRepository = buttonResponseRepository;
        this.mediaRepository = mediaRepository;
        this.uploadPhotoService = uploadPhotoService;
        this.keyboardMapper = keyboardMapper;
    }

    @Override
    @Transactional
    public List<KeyboardPageRequestResponseDto> getAll() {
        return keyboardMapper.toResponseDtoList(
                keyboardPageRepository.findAll().stream().toList());
    }

    @Override
    @Transactional
    public KeyboardPageRequestResponseDto save(KeyboardPageDto dto) {
        // 1) Получаем или создаём страницу
        KeyboardPage page = dto.id() != null
                ? keyboardPageRepository.findById(dto.id())
                : keyboardMapper.toEntity(dto);
        page.setStartPage(dto.startPage());

        // 2) Батчевой выборкой подтягиваем все существующие медиа и кнопки:
        // 2.1) Медиа по URL
        List<String> allUrls = dto.pageButtons().stream()
                .flatMap(b -> b.response().media().stream())
                .map(MediaDto::url)
                .distinct()
                .collect(Collectors.toList());
        List<Media> anyMedia = mediaRepository.findByUrls(allUrls);
        Map<String, Media> existingAnyMedia = anyMedia.stream()
                .collect(Collectors.toMap(
                        Media::getUrl, Function.identity(),
                        (m1, m2) -> m1
                ));
        List<String> toUpload = allUrls.stream()
                .filter(url -> !existingAnyMedia.containsKey(url))
                .collect(Collectors.toList());
        UploadMediaResult uploadResult = uploadPhotoService.uploadPhotos(toUpload);
        Map<String, UploadMedia> uploadedByUrl = uploadResult.getSuccessUploadedMedia().stream()
                .collect(Collectors.toMap(UploadMedia::getUrl, Function.identity()));

        // 2.2) Кнопки по команде (для обновлений без id)
        List<String> allCommands = dto.pageButtons().stream()
                .map(ButtonDto::command)
                .distinct()
                .collect(Collectors.toList());
        List<Button> anyButtons = buttonRepository
                .find("command in ?1", allCommands)
                .list();
        Map<String, Button> existingAnyButtons = anyButtons.stream()
                .collect(Collectors.toMap(
                        Button::getCommand, Function.identity(),
                        (b1, b2) -> b1
                ));

        // 3) Убираем старые кнопки (orphanRemoval удалит их из БД)
        page.getPageButtons().clear();

        // 4) Обрабатываем DTO-кнопки
        List<Button> newBtns = new ArrayList<>(dto.pageButtons().size());
        for (ButtonDto btnDto : dto.pageButtons()) {
            // 4.1) Выбираем существующую или новую кнопку
            Button btn;
            if (btnDto.id() != null) {
                btn = buttonRepository.findById(btnDto.id());
            } else if (existingAnyButtons.containsKey(btnDto.command())) {
                btn = existingAnyButtons.get(btnDto.command());
            } else {
                btn = new Button();
            }

            // 4.2) Апдейт полей кнопки
            btn.setCommand(btnDto.command());
            btn.setParentPage(page);
            if (btnDto.nextKeyboardPageId() != null) {
                btn.setNextKeyboardPage(
                        keyboardPageRepository.findById(btnDto.nextKeyboardPageId())
                );
            } else {
                btn.setNextKeyboardPage(null);
            }

            // 4.3) Обрабатываем ответ
            ButtonResponseDto rDto = btnDto.response();
            ButtonResponse resp = rDto.id() != null
                    ? buttonResponseRepository.findById(rDto.id())
                    : new ButtonResponse();
            resp.setText(rDto.text());
            if (resp.getMedia() == null) {
                resp.setMedia(new ArrayList<>());
            }

            // 4.4) Собираем URL только этой кнопки
            List<String> buttonUrls = rDto.media().stream()
                    .map(MediaDto::url)
                    .collect(Collectors.toList());

            // 4.5) Составляем карту уже «своих» медиа для обновления orderIndex
            Map<String, Media> existingOwn = resp.getMedia().stream()
                    .collect(Collectors.toMap(
                            Media::getUrl, Function.identity(),
                            (m1, m2) -> m1
                    ));

            // 4.6) Строим список Media для этой кнопки
            List<Media> newMediaList = new ArrayList<>(buttonUrls.size());
            int order = 0;
            for (String url : buttonUrls) {
                Media m;
                if (existingOwn.containsKey(url)) {
                    m = existingOwn.get(url);
                } else if (existingAnyMedia.containsKey(url)) {
                    // клонируем запись из БД
                    Media template = existingAnyMedia.get(url);
                    m = new Media();
                    m.setUrl(template.getUrl());
                    m.setAttachment(template.getAttachment());
                    m.setButtonResponse(resp);
                } else {
                    // новое — берём из результата upload
                    UploadMedia up = uploadedByUrl.get(url);
                    m = new Media();
                    m.setUrl(up.getUrl());
                    m.setAttachment(up.getAttachment());
                    m.setButtonResponse(resp);
                }
                m.setOrderIndex(order++);
                newMediaList.add(m);
            }

            // 4.7) Обновляем коллекцию media
            resp.getMedia().clear();
            resp.getMedia().addAll(newMediaList);

            // 4.8) Готовим кнопку в новый список
            btn.setButtonResponse(resp);
            newBtns.add(btn);
        }

        // 5) Привязываем новые/обновлённые кнопки к странице
        page.getPageButtons().addAll(newBtns);

        // 6) Сохраняем всё дерево
        keyboardPageRepository.persistAndFlush(page);
        page.getPageButtons().forEach(button ->{
            if(button.getNextKeyboardPage() == null) {
                button.setNextKeyboardPage(button.getParentPage());
            }
        });
        keyboardPageRepository.persistAndFlush(page);
        return keyboardMapper.toResponseDto(page);
    }

    @Override
    @Transactional
    public KeyboardPageRequestResponseDto updateKeyboardPage(Long id, KeyboardPageDto dto) {

        if (id == null) {
           throw new IllegalArgumentException("Id for update keyboard page must be null");
        }

        KeyboardPage existing = keyboardPageRepository.findById(id);
        if (existing == null) {
            throw new NotFoundException("KeyboardPage with id=" + id + " not found");
        }

        KeyboardPageDto toSave = KeyboardPageDto.builder()
                .id(id)
                .startPage(dto.startPage())
                .pageButtons(dto.pageButtons())
                .build();


        return save(toSave);
    }

    @Transactional
    @Override
    public KeyboardPageRequestResponseDto getKeyboardPage(Long id) {
        KeyboardPage keyboardPage = keyboardPageRepository.findById(id);
        if (keyboardPage == null) {
            throw new NotFoundException("KeyboardPage with id=" + id + " not found");
        }
        return keyboardMapper.toResponseDto(keyboardPage);
    }

    @Transactional
    @Override
    public void deleteKeyboardPage(Long id) {
        KeyboardPage existing = keyboardPageRepository.findById(id);
        if (existing == null) {
            throw new NotFoundException("KeyboardPage with id=" + id + " not found");
        }
        keyboardPageRepository.delete(existing);
    }
}
