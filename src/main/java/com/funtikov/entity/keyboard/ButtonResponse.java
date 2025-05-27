package com.funtikov.entity.keyboard;

import com.funtikov.entity.AuditableEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "button_response")
public class ButtonResponse extends AuditableEntity {

    @OneToMany(mappedBy = "buttonResponse",
            fetch = LAZY,
            cascade = ALL,
            orphanRemoval = true)
    @Size(max = 10, message = "Max media capacity - 10")
    private List<Media> media;

    @Lob
    @Column(nullable = false)
    private String text;
}
