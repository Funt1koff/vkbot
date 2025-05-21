package com.funtikov.service;

import com.funtikov.dto.game.GameSaveDto;
import com.funtikov.entity.game.Game;
import com.funtikov.exception.GameNotFoundException;

import java.util.List;

public interface GameService {

    List<Game> findAll();

    Game createGame(GameSaveDto dto);

    Game findById(Long id);

    void deleteGameByBotStartCommand(String botStartCommand);

    Game findByBotStartCommand(String botStartCommand) throws GameNotFoundException;

    boolean existsByBotStartCommand(String botStartCommand);

}
