package com.funtikov.service;

import com.funtikov.dto.photo.UploadMediaResult;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public interface UploadPhotoService {

    UploadMediaResult uploadPhotos(List<String> photoUrls);

    UploadMediaResult uploadPhoto(String photoUrl);

    Future<UploadMediaResult> asyncUploadPhoto(String photoUrl);

}
