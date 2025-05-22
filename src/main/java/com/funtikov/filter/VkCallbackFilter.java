package com.funtikov.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funtikov.dto.callback.VkCallback;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Provider
@Priority(Priorities.USER)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
@Slf4j
public class VkCallbackFilter implements ContainerRequestFilter {

    private static final String HEADER_RETRY = "x-retry-counter";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String confirmCode;
    private final String secretKey;

    @Inject
    public VkCallbackFilter(@ConfigProperty(name = "vk.confirm.code") String confirmCode,
                            @ConfigProperty(name = "vk.secret.key") String secretKey) {
        this.confirmCode = confirmCode;
        this.secretKey = secretKey;
    }

    public VkCallbackFilter() {
        this(null, null);
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {

        String path = ctx.getUriInfo().getPath();
        if (!path.startsWith("/callback")) {
            return;
        }
        log.info("Start vk callback filtering");
        String retry = ctx.getHeaderString(HEADER_RETRY);
        if (retry != null) {
            ctx.abortWith(Response.ok(retry).build());
            return;
        }

        InputStream in = ctx.getEntityStream();
        byte[] payload = in.readAllBytes();
        ctx.setEntityStream(new ByteArrayInputStream(payload));

        VkCallback body = objectMapper.readValue(payload, VkCallback.class);

        if ("confirmation".equals(body.getType())) {
            ctx.abortWith(Response.ok(confirmCode).build());
            return;
        }

        if (!secretKey.equals(body.getSecret())) {
            ctx.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("Incorrect secret key")
                            .build()
            );
        }
    }
}
