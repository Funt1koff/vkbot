package com.funtikov.entity.game;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "game_step")
@NoArgsConstructor
@ToString
public class GameStep extends PanacheEntity {

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
