package com.funtikov.repository.impl;

import com.funtikov.entity.keyboard.Media;
import com.funtikov.repository.MediaRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class MediaRepositoryImpl implements MediaRepository {

    @Override
    public List<Media> findByUrls(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return Collections.emptyList();
        }

        return find("url IN ?1", urls).list();
    }

    public Media findByUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        return find("url = ?1", url).firstResult();
    }
}
