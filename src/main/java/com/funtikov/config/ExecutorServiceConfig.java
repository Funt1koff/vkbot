package com.funtikov.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class ExecutorServiceConfig {

    @Produces
    @Named("virtualExecutorService")
    public ExecutorService virtualExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
