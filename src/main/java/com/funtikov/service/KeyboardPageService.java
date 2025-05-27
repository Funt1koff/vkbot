package com.funtikov.service;

import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.entity.keyboard.KeyboardPage;

public interface KeyboardPageService {

    KeyboardPage save(KeyboardPageDto dto);

}
