package com.funtikov.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkObject implements Serializable {

    @JsonProperty("client_info")
    private VkClientInfo vkClientInfo;

    @JsonProperty("message")
    private VkMessage message;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("peer_id")
    private Long peerId;

    @JsonProperty("conversation_message_id")
    private Long conversationMessageId;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("payload")
    private String payload;

}
