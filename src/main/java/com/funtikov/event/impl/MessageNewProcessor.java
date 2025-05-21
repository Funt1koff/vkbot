package com.funtikov.event.impl;

import com.funtikov.command.BotCommand;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.entity.User;
import com.funtikov.event.AbstractEventProcessor;
import com.funtikov.exception.UserNotFoundException;
import com.funtikov.service.UserService;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.vk.api.sdk.objects.callback.Type.MESSAGE_NEW;

@ApplicationScoped
@Slf4j
public class MessageNewProcessor extends AbstractEventProcessor {

    private final UserService userService;

    @Inject
    public MessageNewProcessor(@All List<BotCommand> botCommands,
                              UserService userService) {
        super(botCommands);
        this.userService = userService;
    }

    public MessageNewProcessor() {
        super(null);
        this.userService = null;
    }

    @Override
    public boolean supports(String eventType) {
        return MESSAGE_NEW.getValue().equals(eventType);
    }

    @Override
    public void process(VkCallback callback) {
        Long vkId = callback.getObject().getMessage().getFromId();

        try {
            userService.findByVkId(vkId);
        } catch (UserNotFoundException e) {
            userService.saveUserByVkId(vkId);
        }

        String command = callback.getObject().getMessage().getText();
            botCommands.stream()
                    .filter(botCommand -> botCommand.support(command))
                    .findFirst()
                    .orElse(botCommands.stream().filter(botCommand -> botCommand.support(""))
                            .findFirst().orElseThrow())
                    .processCommand(callback);
    }
}
