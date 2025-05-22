package com.funtikov.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkCallback implements Serializable {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("object")
    private VkObject object;

    public String toString() {
        return new Gson().toJson(this);
    }
}
