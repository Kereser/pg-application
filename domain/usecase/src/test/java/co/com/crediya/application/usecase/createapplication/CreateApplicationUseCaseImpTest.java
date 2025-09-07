package co.com.crediya.application.usecase.createapplication;

import static co.com.crediya.application.usecase.createapplication.DataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.exceptions.EntityNotFoundException;
import co.com.crediya.application.model.exceptions.IllegalValueForArgumentException;
import co.com.crediya.application.model.exceptions.ResourceOwnershipException;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import co.com.crediya.application.model.security.SecurityDetails;
import co.com.crediya.application.model.security.SecurityGateway;
import reactor.blockhound.BlockHound;
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
  @Mock private ApplicationMapper mapper;
  @Mock private AuthGateway authClient;

  @Captor ArgumentCaptor<Application> captor;

  @InjectMocks private CreateApplicationUseCaseImp createApplicationUseCase;

  private CreateApplicationCommand command;
  private UserSummary userSummary;
  private ProductType productType;
  private ApplicationStatus applicationStatus;
  private Application applicationToSave;
  private ApplicationDTOResponse responseDTO;
  private SecurityDetails securityDetails;

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
    applicationStatus =
        new ApplicationStatus(
            UUID.randomUUID(),
            ApplicationStatusName.PENDING,
            ApplicationStatusName.PENDING.getName());

    applicationToSave = Application.builder().amount(new Amount(amount)).build();
    Application savedApplication =
        applicationToSave.toBuilder().id(UUID.randomUUID()).userId(userSummary.id()).build();

    responseDTO =
        new ApplicationDTOResponse(
            savedApplication.getId(),
            amount,
            savedApplication.getUserId(),
            UUID.randomUUID(),
            UUID.randomUUID());

    securityDetails =
        new SecurityDetails(
            savedApplication.getUserId(), new ArrayList<>(Collections.singleton(randomName())));
  }

  @Test
  void shouldCreateApplicationSuccessfully() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(applicationStatus));
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(securityDetails));

    when(applicationRepository.save(any(Application.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    when(mapper.toDTO(any(Application.class))).thenReturn(responseDTO);

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);
    StepVerifier.create(resultMono).expectNext(responseDTO).verifyComplete();

    verify(applicationRepository).save(captor.capture());
    Application appToSave = captor.getValue();

    assertThat(appToSave.getProductType()).isEqualTo(productType);
    assertThat(appToSave.getApplicationStatus()).isEqualTo(applicationStatus);
    assertThat(appToSave.getUserId()).isEqualTo(userSummary.id());
  }

  @Test
  void shouldReturnErrorWhenProductTypeNotFound() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(applicationStatus));

    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.empty());

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  @ParameterizedTest
  @MethodSource("invalidAmountProvider")
  void shouldReturnErrorWhenAmountValidationFails(ProductType pType) {

    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(applicationStatus));

    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.just(pType));

    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.just(securityDetails));

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenOwnershipInvalid() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(applicationStatus));
    when(securityGateway.getDetailsFromContext())
        .thenReturn(
            Mono.just(
                new SecurityDetails(
                    UUID.randomUUID(),
                    new ArrayList<>() {
                      {
                        add(randomName());
                      }
                    })));

    Mono<ApplicationDTOResponse> resMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resMono).expectError(ResourceOwnershipException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenSecContextNotFound() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.just(productType));
    when(appStatusRepository.findByName(ApplicationStatusName.PENDING))
        .thenReturn(Mono.just(applicationStatus));
    when(securityGateway.getDetailsFromContext()).thenReturn(Mono.empty());

    Mono<ApplicationDTOResponse> resMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resMono).expectError(EntityNotFoundException.class).verify();

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
