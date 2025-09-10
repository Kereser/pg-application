package co.com.crediya.application.usecase.handlestatusafterlambdadebtevaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTOInput;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HandleStatusAfterLambdaDebtEvaluationUseCaseImpTest {

  static {
    BlockHound.install();
  }

  @Mock private ApplicationRepository applicationRepository;
  @Mock private ApplicationStatusRepository applicationStatusRepository;

  @InjectMocks private HandleStatusAfterLambdaDebtEvaluationUseCaseImp useCase;

  @Captor private ArgumentCaptor<Application> applicationCaptor;

  private DebtEvaluationDTOInput dto;
  private Application existingApplication;
  private ApplicationStatus newStatus;

  private static final String TYPE = "type";

  @BeforeEach
  void setUp() {
    UUID appId = UUID.randomUUID();
    ApplicationStatus currentStatus =
        ApplicationStatus.builder().name(ApplicationStatusName.PENDING).build();

    dto =
        new DebtEvaluationDTOInput(
            TYPE,
            new DebtEvaluationDTOInput.Payload(appId, ApplicationStatusName.PENDING.getName()));
    newStatus = ApplicationStatus.builder().name(ApplicationStatusName.APPROVED).build();
    existingApplication = Application.builder().id(appId).applicationStatus(currentStatus).build();
  }

  @Test
  void shouldUpdateApplicationStatusSuccessfully() {
    when(applicationRepository.findById(dto.getPayload().getApplicationId()))
        .thenReturn(Mono.just(existingApplication));
    when(applicationStatusRepository.findByName(any(ApplicationStatusName.class)))
        .thenReturn(Mono.just(newStatus));

    when(applicationRepository.save(any(Application.class)))
        .thenAnswer(res -> Mono.just(res.getArguments()[0]));

    Mono<Void> resultMono = useCase.execute(dto);

    StepVerifier.create(resultMono).verifyComplete();

    verify(applicationRepository).save(applicationCaptor.capture());
    assertThat(applicationCaptor.getValue().getApplicationStatus().getName())
        .isEqualTo(newStatus.getName());
  }

  @Test
  void shouldCompleteSilentlyWhenApplicationNotFound() {
    when(applicationRepository.findById(dto.getPayload().getApplicationId()))
        .thenReturn(Mono.empty());
    when(applicationStatusRepository.findByName(any(ApplicationStatusName.class)))
        .thenReturn(Mono.empty());

    Mono<Void> resultMono = useCase.execute(dto);

    StepVerifier.create(resultMono).verifyComplete();

    verify(applicationStatusRepository, times(1)).findByName(any());
    verify(applicationRepository, never()).save(any());
  }

  @Test
  void shouldCompleteSilentlyWhenNewStatusNotFound() {
    when(applicationRepository.findById(dto.getPayload().getApplicationId()))
        .thenReturn(Mono.just(existingApplication));
    when(applicationStatusRepository.findByName(any(ApplicationStatusName.class)))
        .thenReturn(Mono.empty());

    Mono<Void> resultMono = useCase.execute(dto);

    StepVerifier.create(resultMono).verifyComplete();

    verify(applicationRepository, never()).save(any());
  }
}
