package com.funtikov.endpoint.qute;

import com.funtikov.dto.MailingDto;
import com.funtikov.service.MailingService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@Path("/mailing")
@Slf4j
public class MailingEndpoint {

    @Inject
    Template mailingForm;

    @Inject
    Template mailingResult;

    @Inject
    MailingService mailingService;


    @GET
    public TemplateInstance showMailingForm() {
        return mailingForm.instance();
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public TemplateInstance processMailingForm(
            @RestForm("message") String message,
            @RestForm("photos")
            List<FileUpload> photos) {

        try {
            MailingDto dto = new MailingDto();
            dto.setMessage(message);

            mailingService.processMailing(dto, photos);

            return mailingResult
                    .data("successMessage", "Рассылка запущена!");

        } catch (Exception e) {
            log.error("Ошибка при обработке формы рассылки", e);
            return mailingForm
                    .data("errorMessage", "Произошла ошибка при запуске рассылки: " + e.getMessage());
        }
    }
}
