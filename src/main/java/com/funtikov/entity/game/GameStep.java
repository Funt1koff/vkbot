package com.funtikov.entity.game;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "game_step")
@NoArgsConstructor
@Data
@ToString
public class GameStep {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_step_id_seq")
    @SequenceGenerator(name = "game_step_id_seq", sequenceName = "game_step_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private Game game;

    @Column(name = "step_order")
    private Integer stepOrder;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "caption")
    private String caption;

    @OneToMany(mappedBy = "currentStep", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Option> options;
}
