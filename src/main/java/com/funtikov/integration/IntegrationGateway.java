package com.funtikov.integration;

import com.funtikov.exception.IntegrationException;

public interface IntegrationGateway<M, A> {
    A sendMessage(M message) throws IntegrationException;
}
