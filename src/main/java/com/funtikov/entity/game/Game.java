package com.funtikov.entity.game;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "games")
@NoArgsConstructor
@ToString
public class Game extends PanacheEntity implements Serializable {

    @Column(name = "bot_start_command", unique = true, nullable = false)
    private String botStartCommand;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "game")
    private List<GameStep> gameSteps;

    public String getBotStartCommand() {
        return botStartCommand;
    }

    public void setBotStartCommand(String botStartCommand) {
        this.botStartCommand = botStartCommand;
    }

    public List<GameStep> getGameSteps() {
        return gameSteps;
    }

    public void setGameSteps(List<GameStep> gameSteps) {
        this.gameSteps = gameSteps;
    }

    public Long getId() {
        return id;
    }
}
