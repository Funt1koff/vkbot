package com.funtikov.handler;

import com.google.gson.Gson;
import com.vk.api.sdk.objects.photos.responses.PhotoUploadResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class UploadPhotoResponseHandler implements HttpClientResponseHandler<PhotoUploadResponse> {

    private final Gson gson = new Gson();

    @Override
    public PhotoUploadResponse handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        int status = response.getCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream content = entity.getContent();
                     Reader reader = new InputStreamReader(content, StandardCharsets.UTF_8)) {
                    return gson.fromJson(reader, PhotoUploadResponse.class);
                }
            } else {
                throw new HttpException("Response contains no content");
            }
        } else {
            throw new HttpException("Unexpected response status: " + status);
        }
    }
}

