package com.funtikov.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "users_vk_id_index", columnList = "vk_id")
)
@NoArgsConstructor
@Getter
@Setter
public class User extends AuditableEntity {

    @Column(name = "vk_id", unique = true, nullable = false, updatable = false)
    private Long vkId;

    @Column(name = "sending_messages_allowed")
    private boolean sendingMessagesAllowed = true;

    @Column(name = "stop_message_has_been_sent")
    private boolean stopMessageHasBeenSent = false;
}
