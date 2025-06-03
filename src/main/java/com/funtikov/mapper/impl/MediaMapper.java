package com.funtikov.mapper.impl;

import com.funtikov.dto.keyboard.IdReference;
import com.funtikov.dto.keyboard.media.MediaDto;
import com.funtikov.entity.keyboard.ButtonResponse;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.mapper.Mapper;
import com.funtikov.repository.ButtonResponseRepository;
import com.funtikov.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MediaMapper implements Mapper<MediaDto, Media> {

    private final ButtonResponseRepository buttonResponseRepository;
    private final MediaRepository mediaRepository;

    public MediaMapper(ButtonResponseRepository buttonResponseRepository, MediaRepository mediaRepository) {
        this.buttonResponseRepository = buttonResponseRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media toEntity(MediaDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getIdReference().isBackendId()) {
            return mediaRepository.findById(dto.getIdReference().id());
        }

        Media media = new Media();
        media.setOrderIndex(dto.getIdReference().id().intValue());
        media.setUrl(dto.getUrl());

        if (dto.getButtonResponseIdReference().isBackendId()) {
            media.setButtonResponse(buttonResponseRepository.findById(dto.getButtonResponseIdReference().id()));
        } else {
            ButtonResponse transientButtonResponse = createTransientButtonResponse(dto.getButtonResponseIdReference());
            transientButtonResponse.setMedia(List.of(media));
            media.setButtonResponse(transientButtonResponse);
        }

        return media;
    }

    @Override
    public MediaDto toDto(Media entity) {
        return MediaDto.builder()
                .idReference(
                        IdReference.builder()
                                .id(entity.getId())
                                .isBackendId(true)
                                .build())
                .url(entity.getUrl())
                .buttonResponseIdReference(
                        IdReference.builder()
                                .id(entity.getButtonResponse().getId())
                                .isBackendId(true)
                                .build())
                .build();
    }

    @Override
    public List<Media> toEntity(List<MediaDto> dtoList) {
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MediaDto> toDto(List<Media> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ButtonResponse createTransientButtonResponse(IdReference idReference) {
        ButtonResponse buttonResponse = new ButtonResponse();
        buttonResponse.getMetadata().put("IdReference", idReference.id().toString());
        buttonResponse.getMetadata().put("isTransientEntity", "true");
        return buttonResponse;
    }
}
