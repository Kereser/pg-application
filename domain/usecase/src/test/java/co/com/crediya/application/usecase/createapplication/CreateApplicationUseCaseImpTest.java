package co.com.crediya.application.usecase.createapplication;

import static co.com.crediya.application.usecase.createapplication.DataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.CommonConstants;
import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.application.dto.CreateApplicationCommand;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.application.vo.IdNumber;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.eventpublisher.dto.DebtEvaluationDTO;
import co.com.crediya.application.model.eventpublisher.gateway.NotificationEventPublisher;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.exceptions.IllegalValueForArgumentException;
import co.com.crediya.application.model.exceptions.ResourceOwnershipException;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import co.com.crediya.application.model.producttype.vo.ProductName;
import co.com.crediya.application.model.security.SecurityDetails;
import co.com.crediya.application.model.security.SecurityGateway;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateApplicationUseCaseImpTest {
  static {
    BlockHound.install();
  }

  @Mock private ApplicationRepository applicationRepository;
  @Mock private ProductTypeRepository productTypeRepository;
  @Mock private ApplicationStatusRepository appStatusRepository;
  @Mock private SecurityGateway securityGateway;
  @Mock private AuthGateway authClient;
  @Mock private NotificationEventPublisher notificationEventPublisher;
  @Mock private ApplicationMapper mapper;

  @Captor ArgumentCaptor<Application> captor;

  @InjectMocks private CreateApplicationUseCaseImp createApplicationUseCase;

  private CreateApplicationCommand command;
  private UserSummary userSummary;
  private ProductType productType;
  private ApplicationStatus approvedStatus, pendingStatus;
  private ApplicationDTOResponse responseDTO;
  private SecurityDetails securityDetails;

  @Captor private ArgumentCaptor<DebtEvaluationDTO> eventCaptor;
  private Application applicationFromCommand, savedApplication;

  BigDecimal amount = CommonConstants.Amount.FIVE_M;
  BigDecimal minVal = CommonConstants.Amount.ONE_M;
  BigDecimal maxVal = CommonConstants.Amount.TWO_H_M;
  String loanType = CommonConstants.ProductTypeName.VEHICLE_LOAN;

  @BeforeEach
  void setUp() {
    command =
        new CreateApplicationCommand(
            randomIdNumber(), randomBigDecimal(), randomInt(5, 20), loanType);

    userSummary =
        new UserSummary(
            UUID.randomUUID(),
            randomEmail(),
            randomBigDecimal(),
            randomName(),
            randomIdType(),
            randomIdNumber());
    productType =
        ProductType.builder()
            .id(UUID.randomUUID())
            .name(loanType)
            .minAmount(minVal)
            .maxAmount(maxVal)
            .build();
    approvedStatus =
        new ApplicationStatus(
            UUID.randomUUID(),
            ApplicationStatusName.APPROVED,
            ApplicationStatusName.APPROVED.getName());

    Application applicationToSave = Application.builder().amount(new Amount(amount)).build();

    pendingStatus =
        ApplicationStatus.builder()
            .id(UUID.randomUUID())
            .name(ApplicationStatusName.PENDING)
            .build();
    savedApplication =
        applicationToSave.toBuilder()
            .id(UUID.randomUUID())
            .userId(userSummary.id())
            .applicationStatus(pendingStatus)
            .productType(productType)
            .build();
    securityDetails =
        new SecurityDetails(
            savedApplication.getUserId(), new ArrayList<>(Collections.singleton(randomName())));
    responseDTO =
        new ApplicationDTOResponse(
            savedApplication.getId(),
            amount,
            savedApplication.getUserId(),
            UUID.randomUUID(),
            UUID.randomUUID());
    applicationFromCommand = Application.builder().amount(new Amount(command.amount())).build();
  }

  @Test
  void shouldCreateApplicationSuccessfully() {
    // Prerequisites
    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class)))
        .thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));

    // validate ownership
    when(mapper.toEntityCommand(command)).thenReturn(applicationFromCommand);
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(securityDetails));

    // Enrich all apps
    when(applicationRepository.save(any(Application.class)))
        .thenReturn(Mono.just(savedApplication));
    when(applicationRepository.findAllByUserIdAndApplicationStatusId(
            any(UUID.class), any(UUID.class)))
        .thenReturn(Flux.empty());
    when(appStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(pendingStatus));
    when(productTypeRepository.findById(any(UUID.class))).thenReturn(Mono.just(productType));

    // notification
    when(notificationEventPublisher.publishDebtEvaluationQueue(any(DebtEvaluationDTO.class)))
        .thenReturn(Mono.empty());

    // Mapper
    when(mapper.toDTO(any(Application.class))).thenReturn(responseDTO);

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectNext(responseDTO).verifyComplete();

    verify(notificationEventPublisher).publishDebtEvaluationQueue(eventCaptor.capture());
    DebtEvaluationDTO publishedEvent = eventCaptor.getValue();
    assertThat(publishedEvent.usr()).isEqualTo(userSummary);
    assertThat(publishedEvent.applications()).hasSize(1);
    assertThat(publishedEvent.applications().getFirst().getUserId())
        .isEqualTo(savedApplication.getUserId());
    assertThat(publishedEvent.applications().getFirst().getId())
        .isEqualTo(savedApplication.getId());
  }

  @Test
  void shouldReturnErrorWhenProductTypeNotFound() {
    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class))).thenReturn(Mono.empty());
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();
    verify(applicationRepository, never()).save(any(Application.class));
  }

  @ParameterizedTest
  @MethodSource("invalidAmountProvider")
  void shouldReturnErrorWhenAmountValidationFails(ProductType pType) {
    when(mapper.toEntityCommand(command)).thenReturn(applicationFromCommand);
    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class))).thenReturn(Mono.just(pType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(securityDetails));

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();
    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenOwnershipInvalid() {
    SecurityDetails differentUserContext =
        new SecurityDetails(UUID.randomUUID(), List.of(CommonConstants.Security.MANAGER_ROLE));
    when(mapper.toEntityCommand(command)).thenReturn(applicationFromCommand);
    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class)))
        .thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(differentUserContext));

    // Act
    Mono<ApplicationDTOResponse> resMono = createApplicationUseCase.execute(command);

    // Assert
    StepVerifier.create(resMono).expectError(ResourceOwnershipException.class).verify();
    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenSecContextNotFound() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationFromCommand);
    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class)))
        .thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.empty());

    Mono<ApplicationDTOResponse> resMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resMono).expectError(EntityNotFoundException.class).verify();
    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorOnOwnershipValidationFailure() {
    SecurityDetails differentUserContext =
        new SecurityDetails(UUID.randomUUID(), List.of(CommonConstants.Security.MANAGER_ROLE));

    when(authClient.findUserByIdNumber(any(IdNumber.class))).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(any(ProductName.class)))
        .thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(pendingStatus));
    when(appStatusRepository.findByName(ApplicationStatusName.APPROVED))
        .thenReturn(Mono.just(approvedStatus));

    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(differentUserContext));

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(ResourceOwnershipException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  private static Stream<Arguments> invalidAmountProvider() {
    BigDecimal baseAmount = CommonConstants.Amount.TEN_M;

    ProductType produceMinValError =
        ProductType.builder()
            .minAmount(baseAmount)
            .maxAmount(baseAmount.add(BigDecimal.TEN))
            .build();

    ProductType productMaxValError =
        ProductType.builder().minAmount(BigDecimal.ONE).maxAmount(BigDecimal.TEN).build();

    return Stream.of(Arguments.of(produceMinValError), Arguments.of(productMaxValError));
  }
}
