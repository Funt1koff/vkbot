package com.funtikov.repository.impl;

import com.funtikov.entity.game.Game;
import com.funtikov.repository.GameRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class GameRepositoryImpl implements GameRepository {

    @Override
    public Optional<Game> findByBotStartCommand(String botStartCommand) {
        return find("botStartCommand", botStartCommand)
                .withHint("org.hibernate.readOnly", true)
                .firstResultOptional();
    }

    @Override
    public boolean existsByBotStartCommand(String botStartCommand) {
        return count("botStartCommand", botStartCommand) > 0;
    }
}
