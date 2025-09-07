package co.com.crediya.application.model.mapper;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.application.dto.CreateApplicationCommand;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.sqs.SqsSummaryDTO;

public interface ApplicationMapper {
  Application toEntityCommand(CreateApplicationCommand command);

  ApplicationDTOResponse toDTO(Application entity);

  ApplicationSummary toSummary(Application application, UserSummary user);

  SqsSummaryDTO toSqsSummary(Application application, UserSummary user);
}
