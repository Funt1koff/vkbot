package com.funtikov.config;

import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientImpl;
import com.openai.client.okhttp.OkHttpClient;
import com.openai.core.ClientOptions;
import com.openai.core.http.HttpClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static com.openai.core.ClientOptions.PRODUCTION_URL;

@ApplicationScoped
public class OpenAiConfig {

    private final String apiKey;

    public OpenAiConfig(@ConfigProperty(name = "openai.apikey") String apiKey) {
        this.apiKey = apiKey;
    }

    @Produces
    public OpenAIClient openApiClient(ClientOptions options) {
        return new OpenAIClientImpl(options);
    }

    @Produces
    public ClientOptions clientOptions(HttpClient httpClient) {
        return ClientOptions.builder()
                .apiKey(apiKey)
                .httpClient(httpClient)
                .build();
    }

    @Produces
    public HttpClient openAiHttpClient() {
        return OkHttpClient.builder()
                .baseUrl(PRODUCTION_URL)
                .build();
    }
}
