package com.funtikov.command.game;

import com.funtikov.dto.callback.VkCallback;

public interface BotGameCommand {

    void processGameCommand(VkCallback callback);
}
