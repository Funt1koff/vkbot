package com.funtikov.dto.callback;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class VkCallback implements Serializable {

    private String eventId;
    private String secret;
    private Long groupId;
    private String type;
    private VkObject object;

    public String toString() {
        return new Gson().toJson(this);
    }
}
