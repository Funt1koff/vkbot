package com.funtikov.dispatcher;

import com.funtikov.dto.callback.VkCallback;
import com.funtikov.event.VkEventProcessor;
import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class VkEventDispatcher {

    private final List<VkEventProcessor> processors;

    @Inject
    public VkEventDispatcher(@All List<VkEventProcessor> processors) {
        this.processors = processors;
    }

    public VkEventDispatcher() {
        this(null);
    }

    public void dispatch(VkCallback callback) {
        processors.stream()
                .filter(processor -> processor.supports(callback.getType()))
                .findFirst()
                .ifPresentOrElse(
                        processor -> processor.process(callback),
                        () -> log.info("Не найден процессор для события: {}", callback.getType())
                );
    }
}
