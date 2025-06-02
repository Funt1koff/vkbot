package com.funtikov.dto.keyboard.response;

import lombok.Builder;

import java.util.List;

@Builder
public record KeyboardPageRequestResponseDto(
        Long id,
        boolean startPage,
        List<Long> pageButtonIds
) {
}
