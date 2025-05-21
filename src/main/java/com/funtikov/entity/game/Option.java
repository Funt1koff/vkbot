package com.funtikov.entity.game;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "options",
        uniqueConstraints = @UniqueConstraint
                (
                        name = "current_game_step_button_text_current_step_next_step_udx_constraint",
                        columnNames = {"current_game_step_id", "button_text", "next_game_step_id"}))
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Option extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "current_game_step_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private GameStep currentStep;

    @Column(name = "button_text", nullable = false)
    private String buttonText;

    @Column(name = "is_final_option")
    private boolean finalOption = false;

    @ManyToOne
    @JoinColumn(name = "next_game_step_id", referencedColumnName = "id")
    @ToString.Exclude
    private GameStep nextStep;

    public Long getId() {
        return id;
    }
}
