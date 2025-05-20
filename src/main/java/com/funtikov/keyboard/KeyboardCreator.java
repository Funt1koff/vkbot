package com.funtikov.keyboard;

import com.funtikov.command.UserCommandCollection;
import com.vk.api.sdk.objects.messages.Keyboard;

public interface KeyboardCreator {
    boolean supports(UserCommandCollection userCommandCollection);

    Keyboard createKeyboard();

}
