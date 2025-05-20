package com.funtikov.integration.impl;

import com.funtikov.dto.gpt.GptPromptTask;
import com.funtikov.exception.IntegrationException;
import com.funtikov.gpt.ChatGptClient;
import com.funtikov.integration.IntegrationGateway;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ChatGptIntegration implements IntegrationGateway<GptPromptTask, String> {

    private final ChatGptClient client;

    @Override
    public String sendMessage(GptPromptTask message) throws IntegrationException {
        if (message.getPrompt() == null || message.getPrompt().isEmpty() || message.getUserId() == null) {
            throw new IntegrationException("Prompt or user id is missing");
        }

        return client.getResponse(message.getUserId(), message.getPrompt());
    }
}
