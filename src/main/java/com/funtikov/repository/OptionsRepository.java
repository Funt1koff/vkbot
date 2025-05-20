package com.funtikov.repository;

import com.funtikov.entity.game.Option;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

public interface OptionsRepository extends PanacheRepository<Option> {
}
