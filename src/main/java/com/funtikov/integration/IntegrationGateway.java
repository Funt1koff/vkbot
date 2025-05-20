package com.funtikov.integration;

import com.funtikov.exception.IntegrationException;
import com.funtikov.exception.UserNotFoundException;

public interface IntegrationGateway<M, A> {
    A sendMessage(M message) throws IntegrationException, UserNotFoundException;
}
