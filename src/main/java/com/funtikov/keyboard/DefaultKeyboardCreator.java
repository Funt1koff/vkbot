package com.funtikov.keyboard;

import com.funtikov.command.UserCommandCollection;
import com.vk.api.sdk.objects.messages.*;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.funtikov.command.UserCommandCollection.*;


@ApplicationScoped
@Slf4j
public class DefaultKeyboardCreator implements KeyboardCreator {
    @Override
    public boolean supports(UserCommandCollection userCommand) {
        return START == userCommand || RETURN_IDLE == userCommand;
    }

    @Override
    public Keyboard createKeyboard() {
        // Создаем клавиатуру с кнопками, расположенными вертикально (каждая кнопка в своем ряду)
        Keyboard keyboard = new Keyboard();
        keyboard.setOneTime(true);
        List<List<KeyboardButton>> buttons = new ArrayList<>();

        // Кнопка "Расскажи об отряде" в отдельном ряду
        KeyboardButtonActionText action1 = new KeyboardButtonActionText();
        action1.setType(KeyboardButtonActionTextType.TEXT);
        action1.setLabel(ABOUT_OF_SQUAD.getButtonText());
        action1.setPayload("{\"button\": \"1\"}");
        KeyboardButton button1 = new KeyboardButton();
        button1.setAction(action1);
        buttons.add(List.of(button1));

        // Кнопка "Как стать вожатым?" в отдельном ряду
        KeyboardButtonActionText action2 = new KeyboardButtonActionText();
        action2.setType(KeyboardButtonActionTextType.TEXT);
        action2.setLabel(HOW_DO_A_COUNSELOR.getButtonText());
        action2.setPayload("{\"button\": \"2\"}");
        KeyboardButton button2 = new KeyboardButton();
        button2.setAction(action2);
        buttons.add(List.of(button2));

        // Кнопка "Хочу в отряд" в отдельном ряду
        KeyboardButtonActionText action3 = new KeyboardButtonActionText();
        action3.setType(KeyboardButtonActionTextType.TEXT);
        action3.setLabel(WANT_TO_JOIN_SQUAD.getButtonText());
        action3.setPayload("{\"button\": \"3\"}");
        KeyboardButton button3 = new KeyboardButton();
        button3.setAction(action3);
        button3.setColor(KeyboardButtonColor.POSITIVE);
        buttons.add(List.of(button3));

        keyboard.setButtons(buttons);
        keyboard.setInline(false);
        return keyboard;
    }
}
