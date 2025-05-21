package com.funtikov.gpt;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@ApplicationScoped
public class ChatGptClient {

    private final ConcurrentHashMap<Long, List<ChatCompletionMessageParam>> chatCompletionMessages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> cleanupTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final OpenAIClient openAIClient;

    private static final int MAX_MESSAGES = 10;
    private static final long CLEANUP_DELAY_MINUTES = 1;

    @Inject
    public ChatGptClient(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public ChatGptClient() {
        this(null);
    }

    public String getResponse(Long userId, String prompt) {
        if (!chatCompletionMessages.containsKey(userId)) {
            chatCompletionMessages.put(userId, new ArrayList<>());
        }
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(prompt)
                .build();
        ChatCompletionMessageParam userMessageParam = ChatCompletionMessageParam.ofUser(userMessage);

        List<ChatCompletionMessageParam> messages = chatCompletionMessages.get(userId);
        messages.add(userMessageParam);

        ScheduledFuture<?> existingTask = cleanupTasks.get(userId);
        if (existingTask != null) {
            existingTask.cancel(false);
        }
        ScheduledFuture<?> cleanupTask = scheduler.schedule(() -> {
            chatCompletionMessages.remove(userId);
            cleanupTasks.remove(userId);
        }, CLEANUP_DELAY_MINUTES, TimeUnit.MINUTES);
        cleanupTasks.put(userId, cleanupTask);

        ChatCompletionCreateParams createParams = ChatCompletionCreateParams.builder()
                .store(true)
                .model(ChatModel.GPT_4O_MINI)
                .messages(messages)
                .build();

        ChatCompletionMessage response = openAIClient.chat()
                .completions()
                .create(createParams)
                .choices().get(0)
                .message();

        ChatCompletionAssistantMessageParam assistantMessage = ChatCompletionAssistantMessageParam.builder()
                .content(response.content().orElse("Я не смог тебя понять. Попробуй ещё раз"))
                .build();
        ChatCompletionMessageParam assistantMessageParam = ChatCompletionMessageParam.ofAssistant(assistantMessage);

        messages.add(assistantMessageParam);
        chatCompletionMessages.put(userId, messages);

        if (messages.size() >= MAX_MESSAGES) {
            chatCompletionMessages.remove(userId);
            ScheduledFuture<?> task = cleanupTasks.remove(userId);
            if (task != null) {
                task.cancel(false);
            }
        }

        return assistantMessageParam.assistant().orElseThrow().content().get().asText();
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
