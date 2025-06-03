package com.funtikov.endpoint.rest.keyboard;

import com.funtikov.service.KeyboardPageService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/keyboard")
public class KeyboardEndpoint {

    private final KeyboardPageService keyboardPageService;

    @Context
    UriInfo uriInfo;

    public KeyboardEndpoint(KeyboardPageService keyboardPageService) {
        this.keyboardPageService = keyboardPageService;
    }

    @GET
    @Path("/page/all")
    public Response getAllKeyboardPages() {

        return Response.ok().build();
    }

    @POST
    @Path("/page")
    public Response saveKeyboardPage() {
        return Response.ok().build();
    }

    @GET
    @Path("/page/{id}")
    public Response getKeyboardPage() {
        return Response.ok().build();
    }

    @PUT
    @Path("/page/{id}")
    public Response updateKeyboardPage() {
        return Response.ok().build();
    }

    @DELETE
    @Path("/page/{id}")
    public Response deleteKeyboardPage() {
        return Response.ok().build();
    }
}
