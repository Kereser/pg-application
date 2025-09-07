package co.com.crediya.application.usecase.updateapplicationstatus;

import static co.com.crediya.application.usecase.createapplication.DataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Set;
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
import co.com.crediya.application.model.applicationstatus.dto.UpdateApplicationStatusCommand;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.eventpublisher.gateway.NotificationEventPublisher;
import co.com.crediya.application.model.exceptions.DuplicatedInfoException;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import co.com.crediya.application.model.sqs.SqsSummaryDTO;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationStatusUseCaseImpTest {

  static {
    BlockHound.install();
  }

  @Mock private NotificationEventPublisher notificationEventPublisher;
  @Mock private ApplicationStatusRepository applicationStatusRepository;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private ProductTypeRepository productTypeRepository;
  @Mock private AuthGateway authClient;
  @Mock private ApplicationMapper mapper;

  @InjectMocks private UpdateApplicationStatusUseCaseImp useCase;

  @Captor private ArgumentCaptor<Application> applicationCaptor;

  private UpdateApplicationStatusCommand command;
  private Application existingApplication;
  private ApplicationStatus currentStatus;
  private ApplicationStatus newStatus;
  private UserSummary userSummary;
  private SqsSummaryDTO summaryDTO;

  @BeforeEach
  void setUp() {
    UUID appId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    command = new UpdateApplicationStatusCommand(appId, ApplicationStatusName.APPROVED.getName());

    currentStatus =
        ApplicationStatus.builder()
            .id(UUID.randomUUID())
            .name(ApplicationStatusName.PENDING)
            .build();
    newStatus =
        ApplicationStatus.builder()
            .id(UUID.randomUUID())
            .name(ApplicationStatusName.APPROVED)
            .build();

    existingApplication =
        Application.builder()
            .id(appId)
            .userId(userId)
            .applicationStatus(currentStatus)
            .productType(ProductType.builder().id(UUID.randomUUID()).build())
            .build();

    userSummary =
        new UserSummary(
            userId, randomEmail(), randomSalary(), randomName(), randomIdType(), randomIdNumber());

    summaryDTO =
        new SqsSummaryDTO(
            existingApplication.getId(),
            userSummary.email(),
            userSummary.firstName(),
            newStatus.getName());
  }

  @Test
  void shouldUpdateStatusAndPublishEventSuccessfully() {
    when(applicationRepository.findById(command.applicationId()))
        .thenReturn(Mono.just(existingApplication));
    when(applicationStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(newStatus));

    when(applicationRepository.save(any(Application.class)))
        .thenAnswer(argument -> Mono.just(argument.getArguments()[0]));

    when(applicationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(newStatus));
    when(productTypeRepository.findById(any(UUID.class))).thenReturn(Mono.just(new ProductType()));

    when(authClient.findUsersByIdIn(Set.of(existingApplication.getUserId())))
        .thenReturn(Flux.just(userSummary));

    when(mapper.toSqsSummary(any(Application.class), any(UserSummary.class)))
        .thenReturn(summaryDTO);
    when(notificationEventPublisher.publishStatusUpdate(any(SqsSummaryDTO.class)))
        .thenReturn(Mono.empty());

    Mono<Void> resultMono = useCase.execute(command);

    StepVerifier.create(resultMono).verifyComplete();

    verify(applicationRepository).save(applicationCaptor.capture());
    assertThat(applicationCaptor.getValue().getApplicationStatus()).isEqualTo(newStatus);

    verify(notificationEventPublisher).publishStatusUpdate(summaryDTO);
  }

  @Test
  void shouldReturnErrorWhenApplicationNotFound() {
    when(applicationRepository.findById(command.applicationId())).thenReturn(Mono.empty());
    when(applicationStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(newStatus));

    Mono<Void> resultMono = useCase.execute(command);

    StepVerifier.create(resultMono).expectError(EntityNotFoundException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenNewStatusIsSameAsCurrent() {
    UpdateApplicationStatusCommand duplicateCommand =
        new UpdateApplicationStatusCommand(
            existingApplication.getId(), ApplicationStatusName.PENDING.getName());

    when(applicationRepository.findById(duplicateCommand.applicationId()))
        .thenReturn(Mono.just(existingApplication));
    when(applicationStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(currentStatus));

    Mono<Void> resultMono = useCase.execute(duplicateCommand);

    StepVerifier.create(resultMono).expectError(DuplicatedInfoException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }
}
