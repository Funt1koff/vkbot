package com.funtikov.service;

import com.funtikov.dto.callback.VkCallback;

public interface CallbackService {
    void processCallback(VkCallback callback);
}
