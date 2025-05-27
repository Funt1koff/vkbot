package com.funtikov.entity.keyboard;

import com.funtikov.entity.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "media")
public class Media extends AuditableEntity {

    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "button_response_id")
    public ButtonResponse buttonResponse;

    private String url;
    private String attachment;

}
