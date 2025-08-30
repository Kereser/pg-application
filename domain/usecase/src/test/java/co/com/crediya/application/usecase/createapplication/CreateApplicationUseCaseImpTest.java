package co.com.crediya.application.usecase.createapplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import co.com.crediya.application.model.exceptions.IllegalValueForArgumentException;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
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
  @Mock private ApplicationMapper mapper;
  @Mock private AuthGateway authClient;

  @InjectMocks private CreateApplicationUseCaseImp createApplicationUseCase;

  private CreateApplicationCommand command;
  private UserSummary userSummary;
  private ProductType productType;
  private ApplicationStatus applicationStatus;
  private Application applicationToSave;
  private Application savedApplication;
  private ApplicationDTOResponse responseDTO;

  BigDecimal amount = new BigDecimal("5000");
  String vehicleLoanStr = "VEHICLE_INVERSION";

  @BeforeEach
  void setUp() {
    command = new CreateApplicationCommand("123332", amount, 12, vehicleLoanStr);

    userSummary = new UserSummary(UUID.randomUUID(), "12345", "test@test.com", "CC", "123456");
    productType =
        ProductType.builder()
            .id(UUID.randomUUID())
            .name("CREDITO_LIBRE_INVERSION")
            .minAmount(new BigDecimal("1000"))
            .maxAmount(new BigDecimal("10000"))
            .build();
    applicationStatus = new ApplicationStatus(UUID.randomUUID(), "PENDING", "Pending");

    applicationToSave = Application.builder().amount(new Amount(amount)).build();
    savedApplication = applicationToSave.toBuilder().id(UUID.randomUUID()).build();

    responseDTO =
        new ApplicationDTOResponse(
            savedApplication.getId(),
            amount,
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID());
  }

  @Test
  void shouldCreateApplicationSuccessfully() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.just(productType));
    when(appStatusRepository.findPending()).thenReturn(Mono.just(applicationStatus));

    when(applicationRepository.save(any(Application.class)))
        .thenReturn(Mono.just(savedApplication));

    when(mapper.toDTO(savedApplication)).thenReturn(responseDTO);

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectNext(responseDTO).verifyComplete();

    verify(applicationRepository).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenProductTypeNotFound() {
    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(appStatusRepository.findPending()).thenReturn(Mono.just(applicationStatus));

    when(productTypeRepository.findByName(anyString())).thenReturn(Mono.empty());

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }

  @Test
  void shouldReturnErrorWhenAmountValidationFails() {
    ProductType productWithInvalidLimits =
        ProductType.builder()
            .minAmount(new BigDecimal("6000"))
            .maxAmount(new BigDecimal("10000"))
            .build();

    when(mapper.toEntityCommand(command)).thenReturn(applicationToSave);

    when(authClient.findUserByIdNumber(anyString())).thenReturn(Mono.just(userSummary));
    when(productTypeRepository.findByName(anyString()))
        .thenReturn(Mono.just(productWithInvalidLimits));
    when(appStatusRepository.findPending()).thenReturn(Mono.just(applicationStatus));

    Mono<ApplicationDTOResponse> resultMono = createApplicationUseCase.execute(command);

    StepVerifier.create(resultMono).expectError(IllegalValueForArgumentException.class).verify();

    verify(applicationRepository, never()).save(any(Application.class));
  }
}
