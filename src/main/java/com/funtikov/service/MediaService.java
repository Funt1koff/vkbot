package com.funtikov.service;

import com.funtikov.dto.keyboard.media.MediaDto;
import com.funtikov.entity.keyboard.Media;
import com.funtikov.exception.MediaExistException;

import java.util.List;

public interface MediaService {

    List<Media> saveMediaList(List<MediaDto> dtoList);

    Media saveMedia(MediaDto mediaDto) throws MediaExistException;

    Media saveMedia(Media media) throws MediaExistException;

}
