package com.funtikov.service;

import com.funtikov.entity.User;
import com.funtikov.exception.UserNotFoundException;

import java.util.List;

public interface UserService {

    User findByVkId(Long vkId) throws UserNotFoundException;

    List<Long> findAllVkIdBySendingMessagesAllowedTrue();
}
