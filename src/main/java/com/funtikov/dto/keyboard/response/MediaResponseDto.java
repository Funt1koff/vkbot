package com.funtikov.dto.keyboard.response;

import lombok.Builder;

@Builder
public record MediaResponseDto(
        Long id,
        Long currentButtonResponseId,
        String url,
        Integer orderIndex
) {
}
