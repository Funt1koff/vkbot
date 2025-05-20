package com.funtikov.exception;

public class GameNotFoundException extends ResourceNotFoundException {

    public GameNotFoundException(String gameStartCommand) {
        super(String.format("Game with start command '%s' not found", gameStartCommand));
    }

}
