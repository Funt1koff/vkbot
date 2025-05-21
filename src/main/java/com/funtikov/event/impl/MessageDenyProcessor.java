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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.vk.api.sdk.objects.callback.Type.MESSAGE_ALLOW;

@ApplicationScoped
@Slf4j
public class MessageDenyProcessor extends AbstractEventProcessor {

    private final UserService userService;

    @Inject
    public MessageDenyProcessor(@All List<BotCommand> botCommands,
                                UserService userService) {
        super(botCommands);
        this.userService = userService;
    }

    public MessageDenyProcessor() {
        super(null);
        this.userService = null;
    }

    @Override
    public boolean supports(String eventType) {
        return MESSAGE_ALLOW.getValue().equals(eventType);
    }

    @Override
    @Transactional
    public void process(VkCallback callBack) {
        User user = null;

        try {
            user = userService.findByVkId(callBack.getObject().getUserId());
        } catch (UserNotFoundException e) {
            log.error("Error process game command ", e);
        }

        if (user == null) {
            user = new User();
            user.setVkId(callBack.getObject().getUserId());
            user.setSendingMessagesAllowed(false);
        }

        if (!user.isSendingMessagesAllowed()) {
            user.setSendingMessagesAllowed(false);
            userService.saveUser(user);
        }

        log.info("Пользователь: {} разрешил сообщения от бота", callBack.getObject().getUserId());
    }
}
