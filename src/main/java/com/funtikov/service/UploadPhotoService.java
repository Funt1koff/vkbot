package com.funtikov.service;

import java.nio.file.Path;
import java.util.List;

public interface UploadPhotoService {
    List<String> uploadPhotos(List<String> photoUrls);
    List<String> uploadPhotoFromLocalFiles(List<Path> localFilePath);

}
