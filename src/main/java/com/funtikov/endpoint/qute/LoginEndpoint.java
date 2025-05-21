package com.funtikov.endpoint.qute;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

@Path("/login")
public class LoginEndpoint {

    @Inject
    Template login;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showLoginForm(@Context UriInfo uriInfo) {
        boolean error = uriInfo.getQueryParameters().containsKey("error");
        return login.data("error", error);
    }
}
