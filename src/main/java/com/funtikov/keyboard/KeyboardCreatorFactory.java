package com.funtikov.keyboard;

import com.funtikov.command.UserCommandCollection;
import com.funtikov.dto.callback.VkCallback;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class KeyboardCreatorFactory {
    private final List<KeyboardCreator> keyboardCreatorList;

    public KeyboardCreator getKeyboardCreator(VkCallback callback) {
        UserCommandCollection userCommand = UserCommandCollection
                .getCommandCollectionByButtonText(callback.getObject().getMessage().getText());

        return keyboardCreatorList.stream()
                .filter(keyboardCreator -> keyboardCreator.supports(userCommand))
                .findFirst()
                .orElse(new DefaultKeyboardCreator());
    }

    public KeyboardCreator getKeyboardCreator(UserCommandCollection userCommand) {
        return keyboardCreatorList.stream()
                .filter(keyboardCreator -> keyboardCreator.supports(userCommand))
                .findFirst()
                .orElse(new DefaultKeyboardCreator());
    }
}
