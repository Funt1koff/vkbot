package com.funtikov.dto.keyboard.button;

import com.funtikov.dto.keyboard.IdReference;
import com.funtikov.dto.keyboard.button.response.ButtonResponseDto;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ButtonDto {

    @NotNull(message = "Id reference must be null")
    private IdReference idReference;

    @NotNull(message = "Id reference must be null")
    @NotEmpty(message = "Id reference must be empty")
    @NotBlank(message = "Id reference must be blank")
    private String command;

    @NotNull(message = "Button response must be null")
    private ButtonResponseDto buttonResponse;

    @NotNull(message = "Parent page id reference must be null")
    private IdReference parentPageIdReference;

    @NotNull(message = "Next page id reference must be null")
    private IdReference nextPageIdReference;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
