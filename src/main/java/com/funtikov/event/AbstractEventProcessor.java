package com.funtikov.event;

import com.funtikov.command.BotCommand;
import com.funtikov.dto.callback.VkCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractEventProcessor implements VkEventProcessor {

    protected final List<BotCommand> botCommands;

    protected AbstractEventProcessor(List<BotCommand> botCommands) {
        this.botCommands = botCommands;
    }

    @Override
    public boolean supports(String eventType) {
        return false;
    }

    @Override
    public void process(VkCallback callback) {
        botCommands.stream()
                .filter(botCommand ->
                        botCommand.support(callback
                                .getObject()
                                .getMessage().getText()) || botCommand.support(""))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .processCommand(callback);
    }
}
