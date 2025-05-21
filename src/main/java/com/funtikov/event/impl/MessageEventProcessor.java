package com.funtikov.event.impl;

import com.funtikov.command.BotCommand;
import com.funtikov.command.game.BotGameCommand;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.event.AbstractEventProcessor;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class MessageEventProcessor extends AbstractEventProcessor {

    private final BotGameCommand botGameCommand;

    @Inject
    public MessageEventProcessor(@All List<BotCommand> botCommands,
                                 BotGameCommand botGameCommand) {
        super(botCommands);
        this.botGameCommand = botGameCommand;
    }

    public MessageEventProcessor() {
        super(null);
        this.botGameCommand = null;
    }

    @Override
    public boolean supports(String eventType) {
        return "message_event".equals(eventType);
    }

    @Override
    public void process(VkCallback callback) {
        botGameCommand.processGameCommand(callback);
    }
}
