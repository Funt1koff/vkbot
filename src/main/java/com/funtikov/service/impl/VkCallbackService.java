package com.funtikov.service.impl;

import com.funtikov.dispatcher.VkEventDispatcher;
import com.funtikov.dto.callback.VkCallback;
import com.funtikov.service.CallbackService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@ApplicationScoped
@Slf4j
public class VkCallbackService implements CallbackService {

    private final VkEventDispatcher eventDispatcher;
    private final ExecutorService executorService;

    @Inject
    public VkCallbackService(VkEventDispatcher eventDispatcher,
                             @Named("virtualExecutorService") ExecutorService executorService) {
        this.eventDispatcher = eventDispatcher;
        this.executorService = executorService;
    }

    public VkCallbackService() {
        this(null, null);
    }

    @Override
    public void processCallback(VkCallback callback) {
        executorService.submit(() -> {
            log.debug("Callback processed task received with body: {}", callback);
            eventDispatcher.dispatch(callback);
        });
    }
}
