package com.funtikov.exception.handler;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Map;

@Provider
@Slf4j
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String constraint = e.getConstraintName();
        log.error("ConstraintViolationException detected", e);
        if ("pagebuttons_command_key".equals(constraint)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of(
                            "error", "Button command must be unique. A button with this command already exists.",
                            "message", e.getErrorMessage()
                    ))
                    .build();
        }
        // для остальных — 500
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "error", "Database constraint violation: " + constraint
                ))
                .build();
    }
}
