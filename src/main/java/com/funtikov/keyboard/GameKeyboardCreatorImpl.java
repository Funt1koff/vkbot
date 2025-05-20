package com.funtikov.keyboard;

import com.funtikov.entity.game.Option;
import com.vk.api.sdk.objects.messages.*;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class GameKeyboardCreatorImpl implements GameKeyboardCreator {

    @Override
    public Keyboard createKeyboard(List<Option> options) {

        if (options == null || options.isEmpty()) {
           return new Keyboard();
        }

        Keyboard keyboard = new Keyboard();
        keyboard.setInline(true);
        List<List<KeyboardButton>> keyboardButtons = options.stream().map(option ->
                {
                    KeyboardButton keyboardButton = new KeyboardButton();
                    KeyboardButtonActionCallback keyboardButtonActionCallback = new KeyboardButtonActionCallback();
                    keyboardButtonActionCallback.setLabel(option.getButtonText());
                    keyboardButtonActionCallback.setPayload(option.getId().toString());
                    keyboardButtonActionCallback.setType(KeyboardButtonActionCallbackType.CALLBACK);
                    keyboardButton.setAction(keyboardButtonActionCallback);
                    keyboardButton.setColor(KeyboardButtonColor.PRIMARY);
                    return List.of(keyboardButton);
                })
                .toList();
        keyboard.setButtons(keyboardButtons);
        return keyboard;
    }
}
