package com.funtikov.event.impl;

import com.funtikov.command.BotCommand;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.event.AbstractEventProcessor;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.vk.api.sdk.objects.callback.Type.MESSAGE_TYPING_STATE;

@ApplicationScoped
@Slf4j
public class MessageTypingStateProcessor extends AbstractEventProcessor {

    @Inject
    public MessageTypingStateProcessor(@All List<BotCommand> botCommands) {
        super(botCommands);
    }

    public MessageTypingStateProcessor() {
        super(null);
    }


    @Override
    public boolean supports(String eventType) {
        return MESSAGE_TYPING_STATE.getValue().equals(eventType);
    }

    @Override
    public void process(VkCallback callBack) {

    }
}
