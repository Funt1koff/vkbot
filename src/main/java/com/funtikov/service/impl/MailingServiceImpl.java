package com.funtikov.service.impl;

import com.funtikov.dto.MailingDto;
import com.funtikov.service.MailingService;
import com.funtikov.service.UploadPhotoService;
import com.funtikov.service.UserService;
import com.google.gson.JsonSyntaxException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class MailingServiceImpl implements MailingService {

    private static final Logger log = Logger.getLogger(MailingServiceImpl.class);

    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final UserService userService;
    private final UploadPhotoService uploadPhotoService;

    @Inject
    public MailingServiceImpl(VkApiClient vkApiClient,
                              GroupActor groupActor,
                              UserService userService,
                              UploadPhotoService uploadPhotoService) {
        this.vkApiClient = vkApiClient;
        this.groupActor = groupActor;
        this.userService = userService;
        this.uploadPhotoService = uploadPhotoService;
    }

    public MailingServiceImpl() {
        this(null, null, null, null);
    }

    @Override
    public void processMailing(MailingDto mailingDto, List<FileUpload> photos) {
        List<Path> tempFilePaths = new ArrayList<>();

        try {
            // 1️⃣ Собираем пути к загруженным файлам
            if (photos != null) {
                for (FileUpload photo : photos) {
                    Path uploaded = photo.uploadedFile();
                    tempFilePaths.add(uploaded);
                    log.infof("Временный файл для загрузки: %s", uploaded);
                }
            }

            // Заполняем DTO, чтобы сервис загрузки знал, откуда брать файлы
            mailingDto.setMediaPaths(tempFilePaths);

            // 2️⃣ Проверяем, есть ли контент
            boolean isTextEmpty = mailingDto.getMessage() == null || mailingDto.getMessage().isBlank();
            boolean hasMedia = !tempFilePaths.isEmpty();
            if (isTextEmpty && !hasMedia) {
                log.warn("Нет ни текста, ни медиа для рассылки. Операция прервана.");
                return;
            }

            // 3️⃣ Получаем список получателей
            List<Long> userVkIds = userService.findAllVkIdBySendingMessagesAllowedTrue();
            if (userVkIds.isEmpty()) {
                log.info("Нет пользователей с разрешённой рассылкой. Операция прервана.");
                return;
            }

            // 4️⃣ Загружаем фото и собираем attachment‑строки
            List<String> attachments = hasMedia
                    ? uploadPhotoService.uploadPhotoFromLocalFiles(tempFilePaths)
                    : Collections.emptyList();
            String attachmentsAsString = attachments.isEmpty()
                    ? null
                    : String.join(",", attachments);

            String messageText = mailingDto.getMessage();

            // 5️⃣ Разбиваем на чанки по 100 и шлём
            for (List<Long> batch : createBatches(userVkIds, 100)) {
                try {
                    sendMessageToUsers(batch, attachmentsAsString, messageText);
                } catch (Exception e) {
                    if (!(e instanceof JsonSyntaxException) && !(e instanceof ClientException)) {
                        log.errorf(e, "Ошибка при отправке чанка: %s", batch);
                    }
                }
            }

            log.info("Массовая рассылка завершена.");
        } catch (Exception e) {
            log.error("Произошла ошибка при выполнении рассылки", e);
        } finally {
            // 6️⃣ Удаляем временные файлы
            for (Path path : tempFilePaths) {
                try {
                    Files.deleteIfExists(path);
                    log.infof("Временный файл удалён: %s", path);
                } catch (IOException ioe) {
                    log.warnf("Не удалось удалить %s: %s", path, ioe.getMessage());
                }
            }
        }
    }

    private List<List<Long>> createBatches(List<Long> ids, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += batchSize) {
            batches.add(new ArrayList<>(ids.subList(i, Math.min(i + batchSize, ids.size()))));
        }
        return batches;
    }

    private void sendMessageToUsers(List<Long> userIds, String attachments, String text)
            throws InterruptedException {
        final int rps = 10;
        final int delay = 1000 / rps;
        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);
            try {
                vkApiClient.messages()
                        .sendUserIds(groupActor)
                        .randomId((int) (Math.random() * Integer.MAX_VALUE))
                        .userId(userId)
                        .message(text)
                        .attachment(attachments)
                        .execute();
                log.infof("Сообщение отправлено пользователю: %d", userId);
            } catch (ApiException | ClientException ex) {
                log.errorf("Ошибка при отправке пользователю %d: %s", userId, ex.getMessage());
            }
            // throttle VK API
            if ((i + 1) % rps == 0) {
                Thread.sleep(1000);
            } else {
                Thread.sleep(delay);
            }
        }
    }
}
