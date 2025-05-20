package com.funtikov.service.impl;

import com.funtikov.entity.User;
import com.funtikov.exception.UserNotFoundException;
import com.funtikov.repository.UserRepository;
import com.funtikov.service.UserService;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
