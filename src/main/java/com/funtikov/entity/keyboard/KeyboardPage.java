package com.funtikov.entity.keyboard;

import com.funtikov.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "keyboard_pages")
@Getter
@Setter
public class KeyboardPage extends AuditableEntity {

    @Column(name = "is_start_page", nullable = false)
    private boolean startPage = false;

    @OneToMany(
            mappedBy    = "parentPage",
            cascade     = CascadeType.ALL,
            orphanRemoval = true,
            fetch       = FetchType.EAGER
    )
    @Size(min = 1, max = 3, message = "Keyboard page is not have less 1 button and more 3 button")
    private List<Button> pageButtons;

}
