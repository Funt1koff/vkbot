package com.funtikov.service;

import com.funtikov.entity.game.Game;
import com.funtikov.exception.GameNotFoundException;

public interface GameService {

    Game findByBotStartCommand(String botStartCommand) throws GameNotFoundException;

    boolean existsByBotStartCommand(String botStartCommand);
}
