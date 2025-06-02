package com.funtikov.endpoint.rest;

import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.dto.keyboard.response.KeyboardPageRequestResponseDto;
import com.funtikov.entity.keyboard.KeyboardPage;
import com.funtikov.service.KeyboardPageService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

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

        return Response.ok(keyboardPageService.getAll()).build();
    }

    @POST
    @Path("/page")
    public Response saveKeyboardPage(@Valid KeyboardPageDto dto) {
        KeyboardPageRequestResponseDto saved = keyboardPageService.save(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(saved.id().toString())
                .build();

        return Response.created(location)
                .entity(saved)
                .build();
    }

    @GET
    @Path("/page/{id}")
    public Response getKeyboardPage(@PathParam("id") Long id) {
        KeyboardPageRequestResponseDto keyboardPage = keyboardPageService.getKeyboardPage(id);
        return Response.ok(keyboardPage).build();
    }

    @PUT
    @Path("/page/{id}")
    public Response updateKeyboardPage(@Valid KeyboardPageDto dto, @PathParam("id") Long id) {
        KeyboardPageRequestResponseDto keyboardPage = keyboardPageService.updateKeyboardPage(id, dto);
        return Response.ok(keyboardPage).build();
    }

    @DELETE
    @Path("/page/{id}")
    public Response deleteKeyboardPage(@PathParam("id") Long id) {
        keyboardPageService.deleteKeyboardPage(id);
        return Response.noContent().build();
    }
}
