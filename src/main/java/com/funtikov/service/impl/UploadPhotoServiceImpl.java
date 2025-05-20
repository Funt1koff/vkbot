package com.funtikov.service.impl;

import com.funtikov.handler.UploadPhotoResponseHandler;
import com.funtikov.service.UploadPhotoService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.GetMessagesUploadServerResponse;
import com.vk.api.sdk.objects.photos.responses.PhotoUploadResponse;
import com.vk.api.sdk.objects.photos.responses.SaveMessagesPhotoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UploadPhotoServiceImpl implements UploadPhotoService {

    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final HttpClient httpClient;
    private final UploadPhotoResponseHandler uploadPhotoResponseHandler;

    /**
     * Загрузка фотографий по URL.
     */
    @Override
    public List<String> uploadPhotos(List<String> photoUrls) {
        List<String> attachments = new ArrayList<>();
        if (photoUrls == null || photoUrls.isEmpty()) {
            return attachments;
        }

        for (String photoUrl : photoUrls) {
            if (photoUrl == null || photoUrl.trim().isEmpty()) {
                continue;
            }

            File tempFile = null;
            try {
                // 1. Скачиваем в tempFile
                tempFile = downloadFileFromUrl(photoUrl);
                if (tempFile == null) {
                    // Если не удалось скачать — пропускаем
                    continue;
                }

                // 2. Загружаем во ВКонтакте и получаем attachment
                String attachment = uploadSingleFile(tempFile);
                if (attachment != null) {
                    attachments.add(attachment);
                }
            } catch (Exception e) {
                log.error("Ошибка при загрузке фото: {}", photoUrl, e);
            } finally {
                // 3. Удаляем временный файл
                if (tempFile != null && tempFile.exists()) {
                    boolean deleted = tempFile.delete();
                    if (!deleted) {
                        log.warn("Не удалось удалить временный файл: {}", tempFile);
                    }
                }
            }
        }
        return attachments;
    }

    /**
     * Загрузка фотографий из локальных файлов (Path).
     */
    @Override
    public List<String> uploadPhotoFromLocalFiles(List<Path> localFilePaths) {
        List<String> attachments = new ArrayList<>();
        if (localFilePaths == null || localFilePaths.isEmpty()) {
            return attachments;
        }

        for (Path path : localFilePaths) {
            if (path == null) {
                continue;
            }
            File file = path.toFile();
            if (!file.exists() || !file.canRead()) {
                log.warn("Файл не найден или не доступен для чтения: {}", path);
                continue;
            }

            try {
                // Загружаем файл во ВКонтакте
                String attachment = uploadSingleFile(file);
                if (attachment != null) {
                    attachments.add(attachment);
                }
            } catch (Exception e) {
                log.error("Ошибка при загрузке фото из локального файла: {}", path, e);
            }
        }
        return attachments;
    }

    /**
     * Скачивает файл по URL во временный файл (возвращает File).
     * В случае ошибки — возвращает null.
     */
    private File downloadFileFromUrl(String photoUrl) {
        try {
            URL url = new URL(photoUrl);
            File tempFile = File.createTempFile("vk_photo", ".jpg");
            try (InputStream in = url.openStream();
                 OutputStream out = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (Exception e) {
            log.error("Не удалось скачать файл по URL: {}", photoUrl, e);
            return null;
        }
    }

    /**
     * Загружает единичный файл на сервер ВКонтакте, возвращает attachment-строку или null в случае ошибки.
     */
    private String uploadSingleFile(File file) {
        try {
            // 1. Получаем Upload URL
            GetMessagesUploadServerResponse uploadServerResponse = vkApiClient.photos()
                    .getMessagesUploadServer(groupActor)
                    .execute();
            URI uploadUrl = uploadServerResponse.getUploadUrl();

            // 2. POST-запрос на сервер ВК
            HttpPost httpPost = new HttpPost(uploadUrl);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody(
                            "photo",                     // ключ формы
                            file,
                            ContentType.MULTIPART_FORM_DATA,
                            file.getName()
                    )
                    .build();
            httpPost.setEntity(entity);

            // Ответ Vk при загрузке
            PhotoUploadResponse uploadResponse = httpClient.execute(httpPost, uploadPhotoResponseHandler);

            // 3. Сохраняем фото на серверах ВК
            List<SaveMessagesPhotoResponse> savedPhotos = vkApiClient.photos()
                    .saveMessagesPhoto(groupActor)
                    .server(uploadResponse.getServer())
                    .photo(uploadResponse.getPhoto())
                    .hash(uploadResponse.getHash())
                    .execute();

            // 4. Формируем attachment-строку "photo{ownerId}_{photoId}"
            if (savedPhotos != null && !savedPhotos.isEmpty()) {
                Photo savedPhoto = savedPhotos.get(0);
                return "photo" + savedPhoto.getOwnerId() + "_" + savedPhoto.getId();
            }

        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", file, e);
        }
        return null;
    }
}
