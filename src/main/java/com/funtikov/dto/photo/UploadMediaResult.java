package com.funtikov.dto.photo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UploadMediaResult {

    private final List<UploadMedia> media;

    public List<UploadMedia> getFailedUploadMedia() {
        return media.stream()
                .filter(media -> media.getStatus().equals(UploadStatus.FAILED))
                .toList();
    }

    public List<UploadMedia> getSuccessUploadedMedia() {
        return media.stream()
                .filter(media -> media.getStatus().equals(UploadStatus.SUCCESS))
                .toList();
    }
}
