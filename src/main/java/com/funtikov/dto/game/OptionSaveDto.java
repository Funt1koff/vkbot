package com.funtikov.dto.game;

import lombok.Data;

import java.io.Serializable;

@Data
public class OptionSaveDto implements Serializable {

    private GameStepSaveDto currentStep;

    private String buttonText;
    private boolean finalOption;

    private GameStepSaveDto nextStep;
}
