package com.funtikov.entity.game;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "options",
        uniqueConstraints = @UniqueConstraint
                (
                        name = "current_game_step_button_text_current_step_next_step_udx_constraint",
                        columnNames = {"current_game_step_id", "button_text", "next_game_step_id"}))
@NoArgsConstructor
@Data
@ToString
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_id_seq")
    @SequenceGenerator(name = "option_id_seq", sequenceName = "option_seq", allocationSize = 1)
    private Long id;

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

}
