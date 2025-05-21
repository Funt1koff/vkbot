package com.funtikov.event.impl;

import com.funtikov.command.BotCommand;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.event.AbstractEventProcessor;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.vk.api.sdk.objects.callback.Type.MESSAGE_READ;

@ApplicationScoped
@Slf4j
public class MessageReadProcessor extends AbstractEventProcessor {
    @Inject
    public MessageReadProcessor(@All List<BotCommand> botCommands) {
        super(botCommands);
    }

    public MessageReadProcessor() {
        super(null);
    }

    @Override
    public boolean supports(String eventType) {
        return MESSAGE_READ.getValue().equalsIgnoreCase(eventType);
    }

    @Override
    public void process(VkCallback callback) {

    }
}
