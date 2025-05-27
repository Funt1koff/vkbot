package com.funtikov.dto.keyboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ButtonDto(
        Long id,
        @NotBlank String command,
        @Valid @NotNull ButtonResponseDto response,
        Long nextKeyboardPageId
) {
}
