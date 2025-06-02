package com.funtikov.dto.keyboard.response;

import lombok.Builder;

@Builder
public record ButtonRequestResponseDto(
        Long id,
        String command,
        Long buttonResponseId,
        Long parentKeyboardPageId,
        Long nextKeyboardPageId
) {}
