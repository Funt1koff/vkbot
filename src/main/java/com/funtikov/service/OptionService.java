package com.funtikov.service;

import com.funtikov.entity.game.Option;
import com.funtikov.exception.OptionNotFoundException;

public interface OptionService {

    Option findOptionById(Long id) throws OptionNotFoundException;

}
