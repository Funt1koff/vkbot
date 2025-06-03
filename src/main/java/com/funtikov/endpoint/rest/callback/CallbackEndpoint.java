package com.funtikov.endpoint.rest.callback;

import com.funtikov.dto.callback.VkCallback;
import com.funtikov.service.CallbackService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/callback")
public class CallbackEndpoint {

    private final CallbackService callbackService;

    public CallbackEndpoint(CallbackService callbackService) {
        this.callbackService = callbackService;
    }


    @POST
    @Produces(TEXT_PLAIN)
    @Consumes({MediaType.APPLICATION_JSON, "application/json; charset=UTF-8"})
    public Response handleCallback(VkCallback callback) {
        callbackService.processCallback(callback);
        return Response.ok("ok").build();
    }
}
