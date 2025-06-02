package com.funtikov.service.impl;

import com.funtikov.dto.keyboard.MediaDto;
import com.funtikov.dto.keyboard.response.MediaResponseDto;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.exception.MediaNotFoundException;
import com.funtikov.mapper.KeyboardMapper;
import com.funtikov.repository.MediaRepository;
import com.funtikov.service.MediaService;
import com.funtikov.service.UploadPhotoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final KeyboardMapper keyboardMapper;
    private final UploadPhotoService uploadPhotoService;

    @Inject
    public MediaServiceImpl(MediaRepository mediaRepository, KeyboardMapper keyboardMapper, UploadPhotoService uploadPhotoService) {
        this.mediaRepository = mediaRepository;
        this.keyboardMapper = keyboardMapper;
        this.uploadPhotoService = uploadPhotoService;
    }


    @Override
    @Transactional
    public List<MediaResponseDto> getAll() {
        return keyboardMapper.toMediaResponseDtoList(mediaRepository.findAll().stream().toList());
    }

    @Override
    @Transactional
    public MediaResponseDto getById(Long id) throws MediaNotFoundException {
        Media media = mediaRepository.findById(id);

        if (media == null) {
            throw new MediaNotFoundException(id);
        }

        return keyboardMapper.toMediaResponseDto(media);
    }

    @Override
    @Transactional
    public MediaResponseDto save(MediaDto mediaDto) {
        String url = mediaDto.url();
        Media existingMedia = mediaRepository.findByUrl(url);

        if (existingMedia != null) {
            Media newMedia = new Media();
            newMedia.setUrl(url);
            newMedia.setAttachment(existingMedia.getAttachment());
            newMedia.setOrderIndex(mediaDto.orderIndex());
        }
        return null;
    }

    @Override
    @Transactional
    public MediaResponseDto update(MediaDto mediaDto) {
        return null;
    }

    @Override
    @Transactional
    public MediaResponseDto delete(MediaDto mediaDto) {
        return null;
    }
}
