package com.funtikov.exception;

public class OptionNotFoundException extends ResourceNotFoundException {

    public OptionNotFoundException(Long optionId) {
        super("Option with id '" + optionId + "' not found");
    }
}
