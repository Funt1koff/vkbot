package com.funtikov.keyboard;

import com.funtikov.command.UserCommandCollection;
import com.funtikov.dto.callback.VkCallback;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class KeyboardCreatorFactory {

    private final List<KeyboardCreator> keyboardCreatorList;

    @Inject
    public KeyboardCreatorFactory(@All List<KeyboardCreator> keyboardCreatorList) {
        this.keyboardCreatorList = keyboardCreatorList;
    }

    public KeyboardCreatorFactory() {
        this.keyboardCreatorList = new ArrayList<>();
    }

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
