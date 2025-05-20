package com.funtikov.dto.callback;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class VkObject implements Serializable {

    private VkClientInfo vkClientInfo;
    private VkMessage message;
    private Long userId;
    private Long peerId;
    private Long conversationMessageId;
    private String eventId;
    private String payload;

}
