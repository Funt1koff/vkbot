package com.funtikov.dto.game;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class GameStepSaveDto implements Serializable {
    /**
     * Уникальный ключ, чтобы мы могли связать шаги между собой
     */
    private String stepKey = UUID.randomUUID().toString();

    private Integer stepOrder;
    private String imageUrl;
    private String caption;
    private List<OptionSaveDto> options;
}
