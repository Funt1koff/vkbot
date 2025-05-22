package com.funtikov.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkMessage implements Serializable {

    @JsonProperty("date")
    private long date;

    @JsonProperty("from_id")
    private long fromId;

    @JsonProperty("attachments")
    private List<Object> attachments;

    @JsonProperty("is_hidden")
    private boolean isHidden;

    @JsonProperty("version")
    private long version;

    @JsonProperty("out")
    private long out;

    @JsonProperty("conversation_message_id")
    private long conversationMessageId;

    @JsonProperty("peer_id")
    private long peerId;

    @JsonProperty("important")
    private boolean important;

    @JsonProperty("fwd_messages")
    private List<String> fwdMessages;

    @JsonProperty("id")
    private long id;

    @JsonProperty("text")
    private String text;

    @JsonProperty("random_id")
    private long randomId;

}
