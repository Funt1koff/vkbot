package com.funtikov.dto.callback;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class VkClientInfo implements Serializable {

    private boolean keyboard;
    private boolean inlineKeyboard;
    private List<String> buttonActions;
    private Long langId;
    private boolean carousel;

}
