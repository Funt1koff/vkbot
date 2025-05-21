package com.funtikov.service;

import com.funtikov.dto.MailingDto;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public interface MailingService {
    void processMailing(MailingDto mailingDto, List<FileUpload> photos);
}
