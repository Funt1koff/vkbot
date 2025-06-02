package com.funtikov.repository;

import com.funtikov.entity.keyboard.Media;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;

public interface MediaRepository extends PanacheRepository<Media> {

    List<Media> findByUrls(List<String> urls);

    Media findByUrl(String url);

}
