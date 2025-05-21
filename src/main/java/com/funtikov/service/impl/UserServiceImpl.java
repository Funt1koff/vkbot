package com.funtikov.service.impl;

import com.funtikov.entity.User;
import com.funtikov.exception.UserNotFoundException;
import com.funtikov.repository.UserRepository;
import com.funtikov.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserServiceImpl() {
        this.userRepository = null;
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        log.debug("Saving user with vkId '{}'", user.getVkId());
        user.persistAndFlush();
        return user;
    }

    @Override
    @Transactional
    public User saveUserByVkId(Long vkId) {
        log.debug("Saving user with vkId '{}'", vkId);
        User user = new User();
        user.setVkId(vkId);
        user.persistAndFlush();
        return user;
    }

    @Override
    @Transactional
    public User findByVkId(Long vkId) throws UserNotFoundException {
        log.debug("Find user by vkId {}", vkId);
        return userRepository.findByVkId(vkId)
                .orElseThrow(() -> new UserNotFoundException(vkId));
    }

    @Override
    @Transactional
    public List<Long> findAllVkIdBySendingMessagesAllowedTrue() {
        log.debug("Find all users by sendingMessagesAllowed 'true'");
        return userRepository.findAllVkIdBySendingMessagesAllowedTrue();
    }
}
