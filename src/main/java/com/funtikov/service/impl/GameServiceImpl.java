package com.funtikov.service.impl;

import com.funtikov.entity.game.Game;
import com.funtikov.exception.GameNotFoundException;
import com.funtikov.repository.GameRepository;
import com.funtikov.service.GameService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    @Transactional
    public Game findByBotStartCommand(String botStartCommand) throws GameNotFoundException {
        log.debug("Finding game by bot start command {}", botStartCommand);
        return gameRepository.findByBotStartCommand(botStartCommand)
                .orElseThrow(() -> new GameNotFoundException(botStartCommand));
    }

    @Override
    @Transactional
    public boolean existsByBotStartCommand(String botStartCommand) {
        log.debug("Check exist game by bot start command {}", botStartCommand);
        return gameRepository.existsByBotStartCommand(botStartCommand);
    }
}
