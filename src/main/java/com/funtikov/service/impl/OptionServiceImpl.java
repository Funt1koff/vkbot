package com.funtikov.service.impl;

import com.funtikov.entity.game.Option;
import com.funtikov.exception.OptionNotFoundException;
import com.funtikov.repository.OptionsRepository;
import com.funtikov.service.OptionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionsRepository optionsRepository;

    @Override
    @Transactional
    public Option findOptionById(Long id) throws OptionNotFoundException {
        return Optional.of(optionsRepository.findById(id))
                .orElseThrow(() -> new OptionNotFoundException(id));
    }
}
