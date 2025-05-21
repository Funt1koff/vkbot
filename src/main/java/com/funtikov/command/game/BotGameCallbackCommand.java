package com.funtikov.command.game;

import com.funtikov.command.UserCommandCollection;
import com.funtikov.dto.VkMessageTask;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.entity.game.GameStep;
import com.funtikov.entity.game.Option;
import com.funtikov.exception.IntegrationException;
import com.funtikov.exception.OptionNotFoundException;
import com.funtikov.integration.IntegrationGateway;
import com.funtikov.keyboard.GameKeyboardCreator;
import com.funtikov.keyboard.KeyboardCreatorFactory;
import com.funtikov.service.OptionService;
import com.vk.api.sdk.objects.messages.Keyboard;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class BotGameCallbackCommand implements BotGameCommand {

    private final IntegrationGateway<VkMessageTask, Void> vkIntegrationGateway;
    private final GameKeyboardCreator gameKeyboardCreator;
    private final KeyboardCreatorFactory keyboardCreatorFactory;
    private final OptionService optionService;

    @Inject
    public BotGameCallbackCommand(IntegrationGateway<VkMessageTask, Void> vkIntegrationGateway,
                                  GameKeyboardCreator gameKeyboardCreator,
                                  KeyboardCreatorFactory keyboardCreatorFactory,
                                  OptionService optionService) {
        this.vkIntegrationGateway = vkIntegrationGateway;
        this.gameKeyboardCreator = gameKeyboardCreator;
        this.keyboardCreatorFactory = keyboardCreatorFactory;
        this.optionService = optionService;
    }

    public BotGameCallbackCommand() {
        this(null, null, null, null);
    }

    @Override
    @Transactional
    public void processGameCommand(VkCallback callback) {
        String payload = callback.getObject().getPayload();
        Long optionId = Long.parseLong(payload);
        Option option;
        try {
            option = optionService.findOptionById(optionId);
        } catch (OptionNotFoundException e) {
            log.error("Error process game command ", e);
            return;
        }

        if (option == null) {
            Keyboard keyboard = keyboardCreatorFactory
                    .getKeyboardCreator(UserCommandCollection.RETURN_IDLE)
                    .createKeyboard();

            VkMessageTask errorMessageTask = VkMessageTask.builder()
                    .receivedMessage(callback)
                    .text("Не понял тебя \uD83D\uDE14 \n Попробуй начать заново или попробуй позже")
                    .keyboard(keyboard)
                    .build();

            try {
                vkIntegrationGateway.sendMessage(errorMessageTask);
            } catch (IntegrationException ignored) {
            }
            return;
        }

        GameStep nextStep = option.getNextStep();

        if (nextStep == null && option.isFinalOption()) {

            Keyboard keyboard = keyboardCreatorFactory
                    .getKeyboardCreator(UserCommandCollection.START)
                    .createKeyboard();

            VkMessageTask successMessageTask = VkMessageTask.builder()
                    .text("Поздравляю! Ты прошел игру!")
                    .keyboard(keyboard)
                    .receivedMessage(callback)
                    .build();

            try {
                vkIntegrationGateway.sendMessage(successMessageTask);
            } catch (IntegrationException ignored) {
            }
            return;
        }

        Keyboard inlineKeyboard = gameKeyboardCreator.createKeyboard(Objects.requireNonNull(nextStep).getOptions());

        VkMessageTask gameMessageTask = VkMessageTask.builder()
                .text(nextStep.getCaption())
                .inlineKeyboard(inlineKeyboard)
                .photoUrls(List.of(nextStep.getImageUrl()))
                .build();
        try {
            vkIntegrationGateway.sendMessage(gameMessageTask);
        } catch (IntegrationException ignored) {
        }
    }
}
