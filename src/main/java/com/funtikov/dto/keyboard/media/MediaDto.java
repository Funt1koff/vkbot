package com.funtikov.dto.keyboard.media;

import com.funtikov.dto.keyboard.IdReference;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaDto {

    @NotNull(message = "Id reference must be null")
    private IdReference idReference;

    @NotNull(message = "Button response id reference must be null")
    private IdReference buttonResponseIdReference;

    @NotNull(message = "Media url must be null")
    private String url;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
