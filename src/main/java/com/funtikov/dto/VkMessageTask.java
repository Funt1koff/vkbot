package com.funtikov.dto;

import com.funtikov.dto.callback.VkCallback;
import com.vk.api.sdk.objects.messages.Keyboard;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class VkMessageTask {

    private VkCallback receivedMessage;
    private String text;
    private List<String> photoUrls;
    private Keyboard keyboard;
    private Keyboard inlineKeyboard;
}
