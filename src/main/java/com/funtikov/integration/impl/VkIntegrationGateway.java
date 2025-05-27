package com.funtikov.integration.impl;

import com.funtikov.dto.VkMessageTask;
import com.funtikov.dto.photo.UploadMedia;
import com.funtikov.dto.photo.UploadMediaResult;
import com.funtikov.entity.User;
import com.funtikov.exception.UserNotFoundException;
import com.funtikov.integration.IntegrationGateway;
import com.funtikov.service.UploadPhotoService;
import com.funtikov.service.UserService;
import com.google.gson.JsonSyntaxException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.queries.messages.MessagesSendQueryWithUserIds;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@ApplicationScoped
@Slf4j
public class VkIntegrationGateway implements IntegrationGateway<VkMessageTask, Void> {

    private final VkApiClient vkApiClient;
    private final UploadPhotoService uploadPhotoService;
    private final GroupActor groupActor;
    private final UserService userService;

    @Inject
    public VkIntegrationGateway(VkApiClient vkApiClient,
                                UploadPhotoService uploadPhotoService,
                                GroupActor groupActor,
                                UserService userService) {
        this.vkApiClient = vkApiClient;
        this.uploadPhotoService = uploadPhotoService;
        this.groupActor = groupActor;
        this.userService = userService;
    }

    public VkIntegrationGateway() {
        this(null, null, null, null);
    }

    @Override
    public Void sendMessage(VkMessageTask message)  {
        MessagesSendQueryWithUserIds messagesSendQueryWithUserIds = vkApiClient.messages().sendUserIds(groupActor);
        Long vkUserId = message.getReceivedMessage().getObject().getUserId();

        if (vkUserId == null) {
            vkUserId = message.getReceivedMessage().getObject().getMessage().getFromId();
        }

        User user = null;

        try {
            user = userService.findByVkId(vkUserId);
        } catch (UserNotFoundException e) {
            log.error("Error sending message to user", e);
        }
        if (user == null) {
            log.info("New user {} has been subscribed to bot messages", vkUserId);
            userService.saveUserByVkId(vkUserId);
        }

        FutureTask<UploadMediaResult> asyncUploadPhotoTask = null;

        if (message.getPhotoUrls() != null && !message.getPhotoUrls().isEmpty()) {
            asyncUploadPhotoTask = new FutureTask<>(() -> uploadPhotoService.uploadPhotos(message.getPhotoUrls()));
            asyncUploadPhotoTask.run();
            log.info("Started async upload photos for user: {}", vkUserId);
        }

        Long peerId = message.getReceivedMessage().getObject().getPeerId();

        if (peerId == null) {
            peerId = message.getReceivedMessage().getObject().getMessage().getPeerId();
        }

        Keyboard keyboard = message.getKeyboard() != null ? message.getKeyboard() : message.getInlineKeyboard();
        String text = message.getText();
        Integer randomId = new Random().nextInt(Integer.MAX_VALUE);

        messagesSendQueryWithUserIds.peerId(peerId)
                .userId(vkUserId)
                .keyboard(keyboard)
                .randomId(randomId)
                .message(text);

        if (asyncUploadPhotoTask != null) {
            try {
                List<String> attachments = asyncUploadPhotoTask.get()
                        .getSuccessUploadedMedia()
                        .stream()
                        .map(UploadMedia::getAttachment)
                        .toList();
                log.info("Successfully async uploaded photos for user: {}", vkUserId);
                String attachmentAsString = String.join(", ", attachments);
                messagesSendQueryWithUserIds.attachment(attachmentAsString);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            messagesSendQueryWithUserIds.execute();
        } catch (ApiException | ClientException | JsonSyntaxException e) {

            if (e instanceof JsonSyntaxException || e instanceof ClientException) {
                return null;
            }
            log.info("Error sending message to user: {}", vkUserId);
        }
        return null;
    }
}
