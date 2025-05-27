package com.funtikov.dto.keyboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record ButtonResponseDto(
        Long id,
        @NotBlank String text,
        @Size(max = 10, message = "Media count must be more 10") List<@Valid MediaDto> media
) {
}
