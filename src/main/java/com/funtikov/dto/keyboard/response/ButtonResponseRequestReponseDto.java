package com.funtikov.dto.keyboard.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ButtonResponseRequestReponseDto(
        Long id,
        List<Long> mediaIds,
        String text
) {
}
