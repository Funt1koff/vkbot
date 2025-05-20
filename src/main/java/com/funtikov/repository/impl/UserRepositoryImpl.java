package com.funtikov.repository.impl;

import com.funtikov.entity.User;
import com.funtikov.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<User> findByVkId(Long vkId) {
        return find("vkId", vkId)
                .withHint("org.hibernate.readOnly", true)
                .firstResultOptional();
    }

    @Override
    public List<Long> findAllVkIdBySendingMessagesAllowedTrue() {
        return list("sendingMessagesAllowed", true)
                .stream()
                .map(User::getVkId)
                .toList();
    }
}
