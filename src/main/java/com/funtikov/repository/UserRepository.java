package com.funtikov.repository;

import com.funtikov.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PanacheRepository<User> {

    Optional<User> findByVkId(Long vkId);

    List<Long> findAllVkIdBySendingMessagesAllowedTrue();

}
