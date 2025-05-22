package com.funtikov.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkClientInfo implements Serializable {

    @JsonProperty("keyboard")
    private boolean keyboard;

    @JsonProperty("inline_keyboard")
    private boolean inlineKeyboard;

    @JsonProperty("button_actions")
    private List<String> buttonActions;

    @JsonProperty("lang_id")
    private Long langId;

    @JsonProperty("carousel")
    private boolean carousel;

}
