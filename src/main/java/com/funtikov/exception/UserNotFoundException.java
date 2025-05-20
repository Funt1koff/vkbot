package com.funtikov.exception;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(Long vkId) {
        super(String.format("User not found with idId %d", vkId));
    }

}
