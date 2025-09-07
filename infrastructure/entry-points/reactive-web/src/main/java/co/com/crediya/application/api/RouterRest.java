package co.com.crediya.application.api;

import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.application.api.helper.ApplicationAPIDocs;
import co.com.crediya.application.api.helper.RestConstants;

@Configuration
public class RouterRest {

  @Bean
  public RouterFunction<ServerResponse> routerFunction(Handler applicationHandler) {
    return SpringdocRouteBuilder.route()
        .POST(
            RestConstants.ApplicationAPI.APPLICATIONS,
            applicationHandler::listenSaveApplication,
            ops -> ApplicationAPIDocs.saveApplicationDocs().accept(ops))
        .GET(
            RestConstants.ApplicationAPI.TO_REVIEW,
            applicationHandler::listenGetApplicationsForManualReview,
            ops -> ApplicationAPIDocs.getApplicationsForManualReview().accept(ops))
        .PATCH(
            RestConstants.ApplicationAPI.APPLICATION_ID,
            applicationHandler::listenPatchApplicationWithStatus,
            ops -> ApplicationAPIDocs.patchApplicationStatus().accept(ops))
        .build();
  }
}
