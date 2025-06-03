package com.funtikov.service.impl;

import com.funtikov.dto.keyboard.page.KeyboardPageDto;
import com.funtikov.repository.ButtonRepository;
import com.funtikov.repository.ButtonResponseRepository;
import com.funtikov.repository.KeyboardPageRepository;
import com.funtikov.repository.MediaRepository;
import com.funtikov.service.KeyboardPageService;
import com.funtikov.service.UploadPhotoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class KeyboardPageServiceImpl implements KeyboardPageService {

    private final KeyboardPageRepository keyboardPageRepository;
    private final ButtonRepository buttonRepository;
    private final ButtonResponseRepository buttonResponseRepository;
    private final MediaRepository mediaRepository;
    private final UploadPhotoService uploadPhotoService;

    public KeyboardPageServiceImpl(KeyboardPageRepository keyboardPageRepository,
                                   ButtonRepository buttonRepository,
                                   ButtonResponseRepository buttonResponseRepository,
                                   MediaRepository mediaRepository,
                                   UploadPhotoService uploadPhotoService) {
        this.keyboardPageRepository = keyboardPageRepository;
        this.buttonRepository = buttonRepository;
        this.buttonResponseRepository = buttonResponseRepository;
        this.mediaRepository = mediaRepository;
        this.uploadPhotoService = uploadPhotoService;
    }

}
