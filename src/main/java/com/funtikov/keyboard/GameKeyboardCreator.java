package com.funtikov.keyboard;

import com.funtikov.entity.game.Option;
import com.vk.api.sdk.objects.messages.Keyboard;

import java.util.List;

public interface GameKeyboardCreator {

    Keyboard createKeyboard(List<Option> options);

}
