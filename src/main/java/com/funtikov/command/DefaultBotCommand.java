package com.funtikov.command;

import com.funtikov.dto.VkMessageTask;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.dto.gpt.GptPromptTask;
import com.funtikov.exception.IntegrationException;
import com.funtikov.integration.IntegrationGateway;
import com.funtikov.keyboard.KeyboardCreatorFactory;
import com.vk.api.sdk.objects.messages.Keyboard;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.funtikov.command.UserCommandCollection.DEFAULT_COMMAND;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class DefaultBotCommand implements BotCommand {
    protected final IntegrationGateway<VkMessageTask, Void> vkIntegrationGateway;
    protected final KeyboardCreatorFactory keyboardCreatorFactory;
    private final IntegrationGateway<GptPromptTask, String> chatGptIntegrationGateway;

    @Override
    public boolean support(String command) {
        return DEFAULT_COMMAND.getButtonText().equals(command);
    }

    @Override
    public void processCommand(VkCallback callback) {
        Keyboard keyboard = keyboardCreatorFactory
                .getKeyboardCreator(callback)
                .createKeyboard();
        try {
            GptPromptTask promptTask = GptPromptTask.builder()
                    .userId(callback.getObject().getMessage().getFromId())
                    .prompt(callback.getObject().getMessage().getText())
                    .build();

            String answerGpt = chatGptIntegrationGateway.sendMessage(promptTask);
            VkMessageTask messageTask = VkMessageTask.builder()
                    .text(answerGpt)
                    .keyboard(keyboard)
                    .receivedMessage(callback)
                    .build();

            vkIntegrationGateway.sendMessage(messageTask);
        } catch (Exception e) {
            log.error(e.getMessage());
            defaultPayload(callback, keyboard);
        }
    }

    @Override
    public void defaultPayload(VkCallback callback, Keyboard keyboard) {
        VkMessageTask messageTask = VkMessageTask.builder()
                .keyboard(keyboard)
                .receivedMessage(callback)
                .text("Не понял тебя \uD83D\uDE14 \n Попробуй ответить мне ещё раз или попробуй позже")
                .build();
        try {
            vkIntegrationGateway.sendMessage(messageTask);
        } catch (IntegrationException ignored) {
        }
    }
}
