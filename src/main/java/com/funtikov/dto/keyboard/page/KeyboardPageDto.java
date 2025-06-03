package com.funtikov.dto.keyboard.page;

import com.funtikov.dto.keyboard.IdReference;
import com.funtikov.dto.keyboard.button.ButtonDto;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class KeyboardPageDto {

    @NotNull(message = "Id reference must be null")
    private final IdReference idReference;

    private boolean startPage = false;

    @Size(min = 1, max = 3, message = "Buttons count in keyboard page must be less 1 and more 3")
    private List<ButtonDto> buttons;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
