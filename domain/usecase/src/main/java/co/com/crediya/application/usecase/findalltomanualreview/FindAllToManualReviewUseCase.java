package co.com.crediya.application.usecase.findalltomanualreview;

import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.model.dto.PageDTOResponse;
import reactor.core.publisher.Mono;

public interface FindAllToManualReviewUseCase {
  Mono<PageDTOResponse<ApplicationSummary>> execute(GetApplicationFilteredCommand command);
}
