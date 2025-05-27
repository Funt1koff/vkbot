package com.funtikov.dto.photo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UploadMedia {

    private final UploadStatus status;
    private final UploadMediaFailReason failReason;
    private final String url;
    private final String attachment;

}
