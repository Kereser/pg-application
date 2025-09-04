package co.com.crediya.application.model.mapper;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.dto.CreateApplicationCommand;

public interface ApplicationMapper {
  Application toEntityCommand(CreateApplicationCommand command);

  ApplicationDTOResponse toDTO(Application entity);

  ApplicationSummary toSummary(Application application, UserSummary user);
}
