package com.funtikov.event;


import com.funtikov.dto.callback.VkCallback;

public interface VkEventProcessor {
    boolean supports(String eventType);

    void process(VkCallback callBack);
}
