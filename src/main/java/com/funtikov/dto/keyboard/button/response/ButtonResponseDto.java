package com.funtikov.dto.keyboard.button.response;

import com.funtikov.dto.keyboard.IdReference;
import com.funtikov.dto.keyboard.media.MediaDto;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ButtonResponseDto {
    private IdReference idReference;
    private List<MediaDto> media;
    private String text;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
