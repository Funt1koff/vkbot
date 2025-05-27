package com.funtikov.endpoint.rest;

import com.funtikov.dto.keyboard.KeyboardPageDto;
import com.funtikov.entity.keyboard.KeyboardPage;
import com.funtikov.service.KeyboardPageService;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

    @POST
    public Response saveKeyboardPage(@Valid KeyboardPageDto dto) {
        KeyboardPage saved = keyboardPageService.save(dto);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(saved.id.toString())
                .build();

        return Response.created(location)
                .entity(saved)
                .build();
    }
}
