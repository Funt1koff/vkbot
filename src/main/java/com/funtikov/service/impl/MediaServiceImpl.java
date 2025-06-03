package com.funtikov.service.impl;

import com.funtikov.dto.keyboard.IdReference;
import com.funtikov.dto.keyboard.media.MediaDto;
import com.funtikov.dto.media.UploadMedia;
import com.funtikov.dto.media.UploadMediaResult;
import com.funtikov.entity.keyboard.ButtonResponse;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.exception.MediaExistException;
import com.funtikov.exception.MediaNotFoundException;
import com.funtikov.mapper.Mapper;
import com.funtikov.repository.ButtonResponseRepository;
import com.funtikov.repository.MediaRepository;
import com.funtikov.service.MediaService;
import com.funtikov.service.UploadPhotoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class MediaServiceImpl implements MediaService {

    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private final MediaRepository mediaRepository;
    private final Mapper<MediaDto, Media> mediaMapper;
    private final UploadPhotoService uploadPhotoService;
    private final ButtonResponseRepository buttonResponseRepository;

    public MediaServiceImpl(MediaRepository mediaRepository,
                            Mapper<MediaDto, Media> mediaMapper,
                            UploadPhotoService uploadPhotoService,
                            ButtonResponseRepository buttonResponseRepository) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.uploadPhotoService = uploadPhotoService;
        this.buttonResponseRepository = buttonResponseRepository;
    }

    @Transactional
    @Override
    public List<MediaDto> getAllMedia() {
        return mediaMapper.toDto(mediaRepository.findAll().stream().toList());
    }

    @Transactional
    @Override
    public List<Media> saveMediaList(List<MediaDto> dtoList) {
        List<String> urls = dtoList.stream()
                .map(MediaDto::getUrl)
                .toList();
        List<Media> existingMedia = mediaRepository.findByUrls(urls);
        List<String> notExistingUrls = dtoList.stream()
                .map(MediaDto::getUrl)
                .filter(url -> !existingMedia.stream()
                        .map(Media::getUrl)
                        .toList().contains(url))
                .toList();

        FutureTask<UploadMediaResult> uploadNotExistingUrlsTask =
                new FutureTask<>(() -> uploadPhotoService.uploadPhotos(notExistingUrls));

        FutureTask<List<Media>> copyExistingEntities =
                new FutureTask<>(() -> createMirroredMediaList(dtoList, existingMedia));
        executorService.submit(uploadNotExistingUrlsTask);
        executorService.submit(copyExistingEntities);

        UploadMediaResult uploadMediaResult = null;
        List<Media> mediaList = null;

        try {
            uploadMediaResult = uploadNotExistingUrlsTask.get();
            mediaList = copyExistingEntities.get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error creating result media list", e);
        }
        List<Media> resuleMediaList = resultMediaList(dtoList, mediaList, uploadMediaResult);
        mediaRepository.persist(resuleMediaList);
        return resuleMediaList;
    }

    @Transactional
    @Override
    public Media saveMedia(MediaDto mediaDto) throws MediaExistException {

        Media media = mediaMapper.toEntity(mediaDto);

        Media existingMedia = mediaRepository.findByUrl(media.getUrl());
        if (existingMedia != null) {
            media.setAttachment(existingMedia.getAttachment());
        } else {
            UploadMediaResult uploadMediaResult = uploadPhotoService.uploadPhoto(media.getUrl());
            if (uploadMediaResult.getSuccessUploadedMedia().isEmpty()) {
                media.setUploadFailReason(uploadMediaResult.getFailedUploadMedia().getFirst().getFailReason());
            } else {
                media.setAttachment(uploadMediaResult.getSuccessUploadedMedia().getFirst().getAttachment());
            }
        }

        ButtonResponse buttonResponse = media.getButtonResponse();
        int mediaListSize = buttonResponse.getMedia().size();
        media.setOrderIndex(mediaListSize);
        return saveMedia(media);
    }


    @Transactional
    @Override
    public Media saveMedia(Media media) throws MediaExistException {
        boolean mediaWithUrlExist = mediaRepository.findByUrl(media.getUrl()) != null;

        if (mediaWithUrlExist) {
            throw new MediaExistException(media.getUrl());
        }

        media.persistAndFlush();
        return media;
    }

    @Transactional
    @Override
    public MediaDto updateMedia(MediaDto mediaDto) throws MediaNotFoundException {

        if (!mediaDto.getIdReference().isBackendId()) {
            throw new MediaNotFoundException(mediaDto.getIdReference().id());
        }

        Media foundedMedia = mediaRepository.findById(mediaDto.getIdReference().id());

        boolean isNewUrl = !mediaDto.getUrl().equals(foundedMedia.getUrl());

        if (!isNewUrl) {
            Media mediaWithEqualUrl = mediaRepository.findByUrl(mediaDto.getUrl());
            if (mediaWithEqualUrl != null) {
                foundedMedia.setUrl(mediaWithEqualUrl.getUrl());
                foundedMedia.setAttachment(mediaWithEqualUrl.getAttachment());
            } else {
                UploadMediaResult uploadMediaResult = uploadPhotoService.uploadPhoto(mediaDto.getUrl());
                if (!uploadMediaResult.getSuccessUploadedMedia().isEmpty()) {
                    UploadMedia uploadMedia = uploadMediaResult.getSuccessUploadedMedia().getFirst();
                    foundedMedia.setAttachment(uploadMedia.getAttachment());
                    foundedMedia.setUrl(uploadMedia.getUrl());
                } else {
                    UploadMedia failUploadMedia = uploadMediaResult.getFailedUploadMedia().getFirst();
                    foundedMedia.setUrl(failUploadMedia.getUrl());
                    foundedMedia.setUploadFailReason(failUploadMedia.getFailReason());
                }
            }
        }
        if (foundedMedia.getButtonResponse() != null) {
            foundedMedia.setOrderIndex(mediaDto.getOrderIndex());
        }

        foundedMedia.persistAndFlush();
        return mediaMapper.toDto(foundedMedia);
    }

    @Override
    @Transactional
    public void deleteMedia(Long id) throws MediaNotFoundException {
        if (mediaRepository.findById(id) == null) {
            throw new MediaNotFoundException(id);
        }
        mediaRepository.deleteById(id);
    }

    private ButtonResponse createTransientButtonResponse(IdReference idReference) {
        ButtonResponse buttonResponse = new ButtonResponse();
        buttonResponse.getMetadata().put("IdReference", idReference.id().toString());
        buttonResponse.getMetadata().put("isTransientEntity", "true");
        return buttonResponse;
    }

    private List<Media> createMirroredMediaList(List<MediaDto> dtoList, List<Media> existingMediaList) {
        return dtoList.stream()
                .map(dto -> {

                    Media media = existingMediaList.stream()
                            .filter(existingMedia -> existingMedia.getUrl().equals(dto.getUrl())).findFirst().get();
                    Media mirroredMedia = new Media();
                    mirroredMedia.setUrl(media.getUrl());
                    mirroredMedia.setAttachment(media.getAttachment());
                    ButtonResponse buttonResponse = null;

                    if (dto.getButtonResponseIdReference().isBackendId()) {
                        buttonResponse = buttonResponseRepository.findById(dto.getButtonResponseIdReference().id());
                    }

                    mirroredMedia.setButtonResponse(buttonResponse);
                    return mirroredMedia;
                })
                .collect(Collectors.toList());

    }

    private List<Media> resultMediaList(List<MediaDto> dtoList, List<Media> existingMediaList, UploadMediaResult uploadMediaResult) {
        if (existingMediaList == null || uploadMediaResult == null) {
            return null;
        }
        List<Media> resultMediaList = new ArrayList<>();
        AtomicInteger iterationIndex = new AtomicInteger();
        dtoList.forEach(dto -> {
            String currentUrl = dto.getUrl();
            ButtonResponse buttonResponse = null;
            if (dto.getButtonResponseIdReference().isBackendId()) {
                buttonResponse = buttonResponseRepository.findById(dto.getButtonResponseIdReference().id());
            }
            Media media = existingMediaList.stream()
                    .filter(existingMedia -> existingMedia.getUrl().equals(currentUrl))
                    .findFirst()
                    .orElse(null);
            if (media != null) {
                media.setOrderIndex(iterationIndex.get());
                resultMediaList.add(media);
            } else {
                List<UploadMedia> successUploadMedia = uploadMediaResult.getSuccessUploadedMedia();
                UploadMedia currentUrlMedia = successUploadMedia.stream()
                        .filter(uploadMedia -> uploadMedia.getUrl().equals(currentUrl))
                        .findFirst()
                        .orElse(null);
                if (currentUrlMedia != null) {
                    Media uploadedMedia = new Media();
                    uploadedMedia.setUrl(currentUrlMedia.getUrl());
                    uploadedMedia.setAttachment(currentUrlMedia.getAttachment());
                    uploadedMedia.setButtonResponse(buttonResponse);
                    uploadedMedia.setOrderIndex(iterationIndex.get());
                    resultMediaList.add(uploadedMedia);
                } else {
                    UploadMedia failUploadMedia = uploadMediaResult.getFailedUploadMedia().stream()
                            .filter(uploadMedia -> uploadMedia.getUrl().equals(currentUrl))
                            .findFirst()
                            .orElseThrow(RuntimeException::new);

                    Media uploadedMedia = new Media();
                    uploadedMedia.setUrl(failUploadMedia.getUrl());
                    uploadedMedia.setUploadFailReason(failUploadMedia.getFailReason());
                    uploadedMedia.setButtonResponse(buttonResponse);
                    uploadedMedia.setOrderIndex(iterationIndex.get());
                    resultMediaList.add(uploadedMedia);
                }
            }
            iterationIndex.getAndIncrement();
        });

        return resultMediaList;
    }
}
