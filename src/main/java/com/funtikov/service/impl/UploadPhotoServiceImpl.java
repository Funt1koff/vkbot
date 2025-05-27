package com.funtikov.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funtikov.dto.media.UploadMedia;
import com.funtikov.dto.media.UploadMediaFailReason;
import com.funtikov.dto.media.UploadMediaResult;
import com.funtikov.dto.media.UploadStatus;
import com.funtikov.exception.UploadPhotoException;
import com.funtikov.service.UploadPhotoService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.GetMessagesUploadServerResponse;
import com.vk.api.sdk.objects.photos.responses.PhotoUploadResponse;
import com.vk.api.sdk.objects.photos.responses.SaveMessagesPhotoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@ApplicationScoped
@Slf4j
public class UploadPhotoServiceImpl implements UploadPhotoService {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient okHttpClient;
    private final GroupActor groupActor;
    private final VkApiClient vkApiClient;

    @Inject
    public UploadPhotoServiceImpl(OkHttpClient okHttpClient,
                                  GroupActor groupActor,
                                  VkApiClient vkApiClient) {
        this.okHttpClient = okHttpClient;
        this.groupActor = groupActor;
        this.vkApiClient = vkApiClient;
    }

    public UploadPhotoServiceImpl() {
        this(null,null,null);
    }

    @Override
    public UploadMediaResult uploadPhotos(List<String> photoUrls) {

        List<Future<UploadMediaResult>> futures = photoUrls.stream()
                .map(this::asyncUploadPhoto)
                .toList();

        List<UploadMedia> allMedia = new ArrayList<>();
        for (Future<UploadMediaResult> future : futures) {
            try {
                UploadMediaResult result = future.get();
                allMedia.addAll(result.getMedia());
            } catch (InterruptedException | ExecutionException e) {
                throw new UploadPhotoException("Error in concurrent upload", e);
            }
        }
        return UploadMediaResult.builder()
                .media(allMedia)
                .build();
    }

    @Override
    public Future<UploadMediaResult> asyncUploadPhoto(String photoUrl) {
        return executor.submit(() -> uploadPhoto(photoUrl));
    }

    @Override
    public UploadMediaResult uploadPhoto(String photoUrl) {
        UploadMediaResult.UploadMediaResultBuilder builder = UploadMediaResult.builder();
        UploadMedia.UploadMediaBuilder uploadMediaBuilder = UploadMedia.builder();
        String attachment = null;
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            uploadMediaBuilder.url(photoUrl);
            uploadMediaBuilder.attachment(attachment);
            uploadMediaBuilder.status(UploadStatus.FAILED);
            uploadMediaBuilder.failReason(UploadMediaFailReason.MEDIA_URL_EMPTY);

            return builder.media(List.of(uploadMediaBuilder.build())).build();
        }

        File templFile = downloadFileFromUrl(photoUrl);

        if (templFile == null) {
            uploadMediaBuilder.url(photoUrl);
            uploadMediaBuilder.attachment(attachment);
            uploadMediaBuilder.status(UploadStatus.FAILED);
            uploadMediaBuilder.failReason(UploadMediaFailReason.ERROR_DOWNLOAD_MEDIA);

            return builder.media(List.of(uploadMediaBuilder.build())).build();
        }

        try {
            attachment = uploadPhotoToVkServer(templFile);
        } catch (Exception e) {
            log.error("Error upload photo to vk server", e);
            uploadMediaBuilder.url(photoUrl);
            uploadMediaBuilder.attachment(attachment);
            uploadMediaBuilder.status(UploadStatus.FAILED);
            uploadMediaBuilder.failReason(UploadMediaFailReason.ERROR_UPLOAD_MEDIA);

            return builder.media(List.of(uploadMediaBuilder.build())).build();
        }

        uploadMediaBuilder.url(photoUrl);
        uploadMediaBuilder.attachment(attachment);
        uploadMediaBuilder.status(UploadStatus.SUCCESS);
        boolean deleteTempFile = templFile.delete();
        if (!deleteTempFile) {
            log.error("Error deleting temp file: {}", templFile.getAbsolutePath());
        }
        return builder.media(List.of(uploadMediaBuilder.build())).build();
    }

    private File downloadFileFromUrl(String photoUrl) {
        Request request = new Request.Builder()
                .url(photoUrl)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Не удалось скачать файл, HTTP код: {}", response.code());
                return null;
            }
            ResponseBody body = response.body();

            if (body == null) {
                log.error("Ответ не содержит тело");
                return null;
            }

            File tempFile = File.createTempFile("vk_photo", ".jpg");

            try (BufferedSink sink = Okio.buffer(Okio.sink(tempFile))) {
                sink.writeAll(body.source());
            }

            return tempFile;
        } catch (IOException e) {
            log.error("Не удалось скачать файл по URL: {}", photoUrl, e);
            return null;
        }
    }

    private String uploadPhotoToVkServer(File file) {
        try {
            // 1. Получаем Upload URL
            GetMessagesUploadServerResponse uploadServerResponse = vkApiClient.photos()
                    .getMessagesUploadServer(groupActor)
                    .execute();

            URI uploadUri = uploadServerResponse.getUploadUrl();

            MediaType jpeg = MediaType.parse("image/jpeg");
            RequestBody fileBody = RequestBody.create(file, jpeg);

            RequestBody multipartBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("photo", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(uploadUri.toString())
                    .post(multipartBody)
                    .build();

            // 3. Выполняем запрос и десериализуем ответ
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.error("Не удалось загрузить файл, HTTP код: {}", response.code());
                    return null;
                }
                String json = response.body().string();
                PhotoUploadResponse uploadResponse =
                        objectMapper.readValue(json, PhotoUploadResponse.class);

                // 4. Сохраняем фото на серверах ВК
                List<SaveMessagesPhotoResponse> savedPhotos =
                        vkApiClient.photos()
                                .saveMessagesPhoto(groupActor)
                                .server(uploadResponse.getServer())
                                .photo(uploadResponse.getPhoto())
                                .hash(uploadResponse.getHash())
                                .execute();

                if (savedPhotos != null && !savedPhotos.isEmpty()) {
                    Photo p = savedPhotos.getFirst();
                    return "photo" + p.getOwnerId() + "_" + p.getId();
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", file, e);
        }
        return null;
    }
}
