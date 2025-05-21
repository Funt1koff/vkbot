package com.funtikov.service.impl;

import com.funtikov.entity.game.Option;
import com.funtikov.exception.OptionNotFoundException;
import com.funtikov.repository.OptionsRepository;
import com.funtikov.service.OptionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@ApplicationScoped
@Slf4j
public class OptionServiceImpl implements OptionService {

    private final OptionsRepository optionsRepository;

    @Inject
    public OptionServiceImpl(OptionsRepository optionsRepository) {
        this.optionsRepository = optionsRepository;
    }

    public OptionServiceImpl() {
        this(null);
    }

    @Override
    @Transactional
    public Option findOptionById(Long id) throws OptionNotFoundException {
        return Optional.of(optionsRepository.findById(id))
                .orElseThrow(() -> new OptionNotFoundException(id));
    }
}
