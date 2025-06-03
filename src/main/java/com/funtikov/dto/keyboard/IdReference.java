package com.funtikov.dto.keyboard;

import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IdReference(
        @NotNull Long id,
        boolean isBackendId
) {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
