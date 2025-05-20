package com.funtikov.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class VkConfig {

    private final String apiKey;
    private final Long groupId;

    public VkConfig(@ConfigProperty(name = "vk.api.key") String apiKey,
                    @ConfigProperty(name = "vk.group.id") Long groupId) {
        this.apiKey = apiKey;
        this.groupId = groupId;
    }

    @Produces
    public TransportClient transportClient() {
        return new HttpTransportClient();
    }

    @Produces
    public VkApiClient vkApiClient(TransportClient transportClient) {
        return new VkApiClient(transportClient);
    }

    @Produces
    public GroupActor groupActor() {
        return new GroupActor(groupId, apiKey);
    }
}
