package com.funtikov.dto;

import lombok.Data;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

@Data
public class MailingDto implements Serializable {

    private String message;
    private List<Path> mediaPaths;
}
