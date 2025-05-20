package com.funtikov.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum UserCommandCollection {
    START("Начать"),
    RETURN_IDLE("↩ Вернуться в начало"),
    ABOUT_OF_SQUAD("\uD83E\uDD28 Расскажи об отряде"),
    HOW_DO_A_COUNSELOR("\uD83E\uDD13 Как стать вожатым?"),
    WANT_TO_JOIN_SQUAD("\uD83E\uDD29 Хочу в отряд"),
    DEFAULT_COMMAND(""),
    EXIT_GAME("❌Закончить игру"),
    STOP("Закончить диалог с ботом");
    private final String buttonText;

    public static UserCommandCollection getCommandCollectionByButtonText(String buttonText) {
        return Arrays.stream(UserCommandCollection.values())
                .filter(commandCollection ->
                        commandCollection.getButtonText().equals(buttonText)).findFirst().orElse(RETURN_IDLE);
    }
}
