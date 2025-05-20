package com.funtikov.command;

import com.funtikov.dto.callback.VkCallback;
import com.vk.api.sdk.objects.messages.Keyboard;

public interface BotCommand {
    boolean support(String command);

    void processCommand(VkCallback callback);

    void defaultPayload(VkCallback callback, Keyboard keyboard);
}
