package com.funtikov.exception;

public class MediaExistException extends ResourceExistException {
    public MediaExistException(String url) {
        super(String.format("Media with url '%s' already exists", url));
    }
}
