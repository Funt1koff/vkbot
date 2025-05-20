package com.funtikov.dto.callback;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class VkMessage implements Serializable {

    private long date;
    private long fromId;
    private List<Object> attachments;
    private boolean isHidden;
    private long version;
    private long out;
    private long conversationMessageId;
    private long peerId;
    private boolean important;
    private List<String> fwdMessages;
    private long id;
    private String text;
    private long randomId;

}
