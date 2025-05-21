package com.funtikov.service.impl;

import com.funtikov.dto.game.GameSaveDto;
import com.funtikov.entity.game.Game;
import com.funtikov.exception.GameNotFoundException;
import com.funtikov.mapper.GameMapper;
import com.funtikov.repository.GameRepository;
import com.funtikov.repository.impl.GameRepositoryImpl;
import com.funtikov.service.GameService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    @Inject
    public GameServiceImpl(GameRepository gameRepository,
                           GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    public GameServiceImpl() {
        this(null, null);
    }

    @Override
    @Transactional
    public List<Game> findAll() {
        return gameRepository.listAll();
    }

    @Override
    @Transactional
    public Game createGame(GameSaveDto dto) {
        if (gameRepository.existsByBotStartCommand(dto.getBotStartCommand())) {
            throw new EntityExistsException("Game with command '" + dto.getBotStartCommand() + "' already exists");
        }

        Game game = gameMapper.mapToGame(dto);
        gameRepository.persistAndFlush(game);

        return game;
    }

    @Override
    @Transactional
    public Game findById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteGameByBotStartCommand(String botStartCommand) {
        Game game = gameRepository.findByBotStartCommand(botStartCommand)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена для botStartCommand: " + botStartCommand));
        gameRepository.delete(game);
    }

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
