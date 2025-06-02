package com.funtikov.service;

import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.dto.keyboard.response.KeyboardPageRequestResponseDto;

import java.util.List;

public interface KeyboardPageService {

    List<KeyboardPageRequestResponseDto> getAll();

    KeyboardPageRequestResponseDto save(KeyboardPageDto dto);

    KeyboardPageRequestResponseDto updateKeyboardPage(Long id, KeyboardPageDto dto);

    KeyboardPageRequestResponseDto getKeyboardPage(Long id);

    void deleteKeyboardPage(Long id);

}
