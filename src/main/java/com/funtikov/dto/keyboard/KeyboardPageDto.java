package com.funtikov.dto.keyboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record KeyboardPageDto(
        Long id,
        boolean startPage,

        @Size(min = 1, max = 3, message = "Buttons count must be less 1 and more 3")
        List<@Valid ButtonDto> pageButtons
) {
}
