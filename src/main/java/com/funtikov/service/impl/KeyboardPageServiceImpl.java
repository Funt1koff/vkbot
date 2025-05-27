package com.funtikov.service.impl;

import com.funtikov.dto.keyboard.ButtonDto;
import com.funtikov.dto.keyboard.ButtonResponseDto;
import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.dto.keyboard.MediaDto;
import com.funtikov.dto.media.UploadMediaResult;
import com.funtikov.entity.keyboard.Button;
import com.funtikov.entity.keyboard.ButtonResponse;
import com.funtikov.entity.keyboard.KeyboardPage;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.mapper.KeyboardMapper;
import com.funtikov.repository.ButtonResponseRepository;
import com.funtikov.repository.KeyboardPageRepository;
import com.funtikov.service.KeyboardPageService;
import com.funtikov.service.UploadPhotoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeyboardPageServiceImpl implements KeyboardPageService {

    private final KeyboardPageRepository keyboardPageRepository;
    private final ButtonResponseRepository buttonResponseRepository;
    private final UploadPhotoService uploadPhotoService;
    private final KeyboardMapper keyboardMapper;

    public KeyboardPageServiceImpl(KeyboardPageRepository keyboardPageRepository, ButtonResponseRepository buttonResponseRepository, UploadPhotoService uploadPhotoService, KeyboardMapper keyboardMapper) {
        this.keyboardPageRepository = keyboardPageRepository;
        this.buttonResponseRepository = buttonResponseRepository;
        this.uploadPhotoService = uploadPhotoService;
        this.keyboardMapper = keyboardMapper;
    }


    @Override
    @Transactional
    public KeyboardPage save(KeyboardPageDto dto) {

        KeyboardPage page = dto.id() != null
                ? keyboardPageRepository.findById(dto.id())
                : keyboardMapper.toEntity(dto);
        page.setStartPage(dto.startPage());

        // при обновлении — очищаем старые кнопки (orphanRemoval их потом удалит)
        page.getPageButtons().clear();

        // 2) Для каждой DTO‐кнопки собираем сущность
        for (ButtonDto btnDto : dto.pageButtons()) {
            Button btn = keyboardMapper.toEntity(btnDto);
            btn.setParentPage(page);

            // ссылку на уже существующую страницу, если есть
            if (btnDto.nextKeyboardPageId() != null) {
                KeyboardPage next = keyboardPageRepository.findById(btnDto.nextKeyboardPageId());
                btn.setNextKeyboardPage(next);
            }

            // 3) Обрабатываем ответ кнопки
            ButtonResponseDto rDto = btnDto.response();
            ButtonResponse resp = rDto.id() != null
                    ? buttonResponseRepository.findById(rDto.id())
                    : new ButtonResponse();
            resp.setText(rDto.text());

            if (resp.getMedia() == null) {
                resp.setMedia(new ArrayList<>());
            } else {
                resp.getMedia().clear();
            }

            // 4) Загружаем все URL через ваш сервис и превращаем их в Media
            UploadMediaResult uploadResult =
                    uploadPhotoService.uploadPhotos(
                            rDto.media()
                                    .stream()
                                    .map(MediaDto::url)
                                    .collect(Collectors.toList())
                    );

            uploadResult.getSuccessUploadedMedia()
                    .forEach(u -> {
                        Media m = new Media();
                        m.setUrl(u.getUrl());
                        m.setAttachment(u.getAttachment());
                        m.setButtonResponse(resp);
                        resp.getMedia().add(m);
                    });

            btn.setButtonResponse(resp);
            page.getPageButtons().add(btn);
        }

        // 5) Сохраняем страницу вместе со всем деревом (cascade = ALL + orphanRemoval)
        keyboardPageRepository.persistAndFlush(page);
        return page;
    }
}
