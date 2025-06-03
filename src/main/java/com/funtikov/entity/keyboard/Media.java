package com.funtikov.entity.keyboard;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.funtikov.dto.media.UploadMediaFailReason;
import com.funtikov.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;


@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Entity
@Table(name = "media")
@Getter
@Setter
public class Media extends AuditableEntity {

    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "button_response_id")
    public ButtonResponse buttonResponse;

    private String url;
    private String attachment;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_fail_reason")
    private UploadMediaFailReason uploadFailReason;

}
