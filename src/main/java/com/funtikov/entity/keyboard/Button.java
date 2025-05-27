package com.funtikov.entity.keyboard;

import com.funtikov.entity.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "buttons")
public class Button extends AuditableEntity {

    @Column(name = "command", nullable = false, unique = true)
    private String command;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "button_response_id", nullable = false)
    private ButtonResponse buttonResponse;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "parent_page_id", nullable = false)
    public KeyboardPage parentPage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_keyboard_page_id")
    public KeyboardPage nextKeyboardPage;

}
