package co.com.crediya.application.model.mapper;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.ApplicationUserSummary;
import co.com.crediya.application.model.application.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.application.dto.CreateApplicationCommand;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.sqs.SqsSummaryDTO;

public interface ApplicationMapper {
  Application toEntityCommand(CreateApplicationCommand command);

  ApplicationDTOResponse toDTO(Application entity);

  ApplicationUserSummary toAppUserSummary(Application application, UserSummary user);

  ApplicationSummary toSummary(Application application);

  SqsSummaryDTO toSqsSummary(Application application, UserSummary user);
}
