package com.funtikov.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "users_vk_id_index", columnList = "vk_id")
)
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "vk_id", unique = true, nullable = false, updatable = false)
    private Long vkId;

    @Column(name = "sending_messages_allowed")
    private boolean sendingMessagesAllowed = true;

    @Column(name = "stop_message_has_been_sent")
    private boolean stopMessageHasBeenSent = false;
}
