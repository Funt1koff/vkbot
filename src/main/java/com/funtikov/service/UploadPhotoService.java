package com.funtikov.service;

import com.funtikov.dto.media.UploadMediaResult;

import java.util.List;
import java.util.concurrent.Future;

public interface UploadPhotoService {

    UploadMediaResult uploadPhotos(List<String> photoUrls);

    UploadMediaResult uploadPhoto(String photoUrl);

    Future<UploadMediaResult> asyncUploadPhoto(String photoUrl);

}
