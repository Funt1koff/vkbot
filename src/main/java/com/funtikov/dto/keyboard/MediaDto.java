package com.funtikov.dto.keyboard;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MediaDto(
        @NotBlank String url,
        int orderIndex
) {
}
