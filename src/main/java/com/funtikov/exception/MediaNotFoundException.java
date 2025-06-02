package com.funtikov.exception;

import lombok.experimental.StandardException;

public class MediaNotFoundException extends Exception {

    public MediaNotFoundException(Long id) {
        super(String.format("Media with id '%s' not found", id));
    }
}
