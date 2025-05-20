package com.funtikov.dto.game;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GameSaveDto implements Serializable {

    private String botStartCommand;
    private List<GameStepSaveDto> gameSteps;
}
