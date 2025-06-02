package com.funtikov.service;

import com.funtikov.dto.keyboard.MediaDto;
import com.funtikov.dto.keyboard.response.MediaResponseDto;
import com.funtikov.exception.MediaNotFoundException;

import java.util.List;

public interface MediaService {

    List<MediaResponseDto> getAll();

    MediaResponseDto getById(Long id) throws MediaNotFoundException;

    MediaResponseDto save(MediaDto mediaDto);

    MediaResponseDto update(MediaDto mediaDto);

    MediaResponseDto delete(MediaDto mediaDto);

}
