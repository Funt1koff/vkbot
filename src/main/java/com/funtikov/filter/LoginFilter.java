package com.funtikov.filter;

import io.quarkus.vertx.http.runtime.filters.Filters;
import io.vertx.core.http.HttpMethod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class LoginFilter {

    void init(@Observes Filters filters) {
        filters.register(rc -> {

            rc.addBodyEndHandler(v -> {
                if (rc.request().method() == HttpMethod.POST
                        && rc.request().path().equals("/login")) {
                    String user = rc.request().getParam("j_username");
                    int status = rc.response().getStatusCode();
                    if (status == 302 || status == 303) {
                        log.info("Успешный вход: user={}, time={}",
                                user, java.time.Instant.now());
                    } else {
                        log.warn("Неудачная попытка входа: user={}, status={}, time={}",
                                user, status, java.time.Instant.now());
                    }
                }
            });
            rc.next();
        }, 100);
    }
}
