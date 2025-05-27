package com.funtikov.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class OkHttpConfig {

    @Produces
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }
}
