package com.funtikov.entity.game;

import com.funtikov.entity.AuditableEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "games")
@NoArgsConstructor
@ToString
public class Game extends AuditableEntity implements Serializable {

    @Column(name = "bot_start_command", unique = true, nullable = false)
    private String botStartCommand;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "game")
    private List<GameStep> gameSteps;

    public Long getId() {
        return id;
    }
}
