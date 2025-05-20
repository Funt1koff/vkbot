package com.funtikov.repository;

import com.funtikov.entity.game.Game;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.Optional;

public interface GameRepository extends PanacheRepository<Game> {

    Optional<Game> findByBotStartCommand(String botStartCommand);

    boolean existsByBotStartCommand(String botStartCommand);

}
