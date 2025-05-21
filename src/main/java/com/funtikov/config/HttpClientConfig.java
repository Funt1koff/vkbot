package com.funtikov.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class HttpClientConfig {

    @Produces
    public HttpClient httpClient(RequestConfig requestConfig) {
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Produces
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(40, TimeUnit.SECONDS)
                .build();
    }
}
