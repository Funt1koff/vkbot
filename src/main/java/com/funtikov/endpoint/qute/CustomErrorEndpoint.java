package com.funtikov.endpoint.qute;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

public class CustomErrorEndpoint {

    @Inject
    Template error;
    @GET

    @Path("error")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance handleError() {
        return error.data("errorMessage",
                "Страница не найдена или произошла внутренняя ошибка.");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response redirectToLogin() {
        return Response
                .status(Response.Status.FOUND)
                .location(UriBuilder.fromPath("/login").build())
                .build();
    }
}
