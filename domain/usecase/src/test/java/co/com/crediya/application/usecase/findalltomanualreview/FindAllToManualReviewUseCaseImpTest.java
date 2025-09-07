package co.com.crediya.application.usecase.findalltomanualreview;

import static co.com.crediya.application.usecase.createapplication.DataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
import co.com.crediya.application.model.application.ApplicationSummary;
import co.com.crediya.application.model.application.dto.GetApplicationFilteredCommand;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.application.vo.ApplicationPeriod;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.dto.PageDTOResponse;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FindAllToManualReviewUseCaseImpTest {

  static {
    BlockHound.install();
  }

  @Mock private ApplicationRepository applicationRepository;
  @Mock private ApplicationStatusRepository applicationStatusRepository;
  @Mock private ProductTypeRepository productTypeRepository;
  @Mock private AuthGateway authGateway;
  @Mock private ApplicationMapper mapper;

  @InjectMocks private FindAllToManualReviewUseCaseImp useCase;

  private GetApplicationFilteredCommand command;
  private Application app1, app2;
  private UserSummary user1, user2;
  private ApplicationSummary summary1, summary2;
  private ApplicationStatus pendingStatus;
  private ProductType productType;

  private static final Integer PAGE = 0;
  private static final Integer SIZE = 10;
  BigDecimal amount = new BigDecimal(5_000_000);
  BigDecimal minVal = new BigDecimal(1_000_000);
  BigDecimal maxVal = new BigDecimal(200_000_000);
  String loanType = "VEHICLE_INVERSION";

  @BeforeEach
  void setUp() {
    command =
        GetApplicationFilteredCommand.builder()
            .page(PAGE)
            .size(SIZE)
            .filters(GetApplicationFilteredCommand.ApplicationFilters.builder().build())
            .build();

    pendingStatus =
        ApplicationStatus.builder()
            .id(UUID.randomUUID())
            .name(ApplicationStatusName.PENDING)
            .build();

    productType =
        ProductType.builder()
            .id(UUID.randomUUID())
            .name(loanType)
            .minAmount(minVal)
            .maxAmount(maxVal)
            .build();

    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();

    app1 =
        Application.builder()
            .id(UUID.randomUUID())
            .userId(user1Id)
            .applicationStatus(pendingStatus)
            .productType(productType)
            .amount(new Amount(amount))
            .applicationPeriod(new ApplicationPeriod(5))
            .build();
    app2 =
        Application.builder()
            .id(UUID.randomUUID())
            .userId(user2Id)
            .applicationStatus(pendingStatus)
            .productType(productType)
            .amount(new Amount(amount))
            .applicationPeriod(new ApplicationPeriod(5))
            .build();

    user1 =
        new UserSummary(
            user1Id,
            randomEmail(),
            randomBigDecimal(),
            randomName(),
            randomIdType(),
            randomIdNumber());
    user2 =
        new UserSummary(
            user2Id,
            randomEmail(),
            randomBigDecimal(),
            randomName(),
            randomIdType(),
            randomIdNumber());

    summary1 =
        ApplicationSummary.builder()
            .userId(user1.id())
            .email(user1.email())
            .name(user1.firstName())
            .baseSalary(user1.baseSalary())
            .productType(productType)
            .status(pendingStatus)
            .interestRate(productType.getInterestRate())
            .amount(app1.getAmount().value())
            .applicationPeriod(app1.getApplicationPeriod().value())
            .build();

    summary2 =
        ApplicationSummary.builder()
            .userId(user2.id())
            .email(user2.email())
            .name(user2.firstName())
            .baseSalary(user2.baseSalary())
            .productType(productType)
            .status(pendingStatus)
            .interestRate(productType.getInterestRate())
            .amount(app2.getAmount().value())
            .applicationPeriod(app2.getApplicationPeriod().value())
            .build();
  }

  @Test
  void shouldReturnEnrichedPageWhenApplicationsAreFound() {
    when(applicationStatusRepository.findAllByNameIn(anyList()))
        .thenReturn(Flux.just(pendingStatus));

    when(applicationRepository.findAllFiltered(any())).thenReturn(Flux.just(app1, app2));
    when(applicationRepository.countByFilers(any())).thenReturn(Mono.just(2L));

    when(applicationStatusRepository.findById(any(UUID.class)))
        .thenReturn(Mono.just(pendingStatus));
    when(productTypeRepository.findById(any(UUID.class))).thenReturn(Mono.just(productType));

    when(authGateway.findUsersByIdIn(anySet())).thenReturn(Flux.just(user1, user2));

    when(mapper.toSummary(any(Application.class), eq(user1))).thenReturn(summary1);
    when(mapper.toSummary(any(Application.class), eq(user2))).thenReturn(summary2);

    Mono<PageDTOResponse<ApplicationSummary>> resultMono = useCase.execute(command);

    StepVerifier.create(resultMono)
        .assertNext(
            page -> {
              assertThat(page.total()).isEqualTo(2L);
              assertThat(page.values()).hasSize(2);
              assertThat(page.values()).containsExactlyInAnyOrder(summary1, summary2);
            })
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyPageWhenNoApplicationsAreFound() {
    when(applicationStatusRepository.findAllByNameIn(anyList()))
        .thenReturn(Flux.just(pendingStatus));

    when(applicationRepository.findAllFiltered(any())).thenReturn(Flux.empty());
    when(applicationRepository.countByFilers(any())).thenReturn(Mono.just(0L));

    Mono<PageDTOResponse<ApplicationSummary>> resultMono = useCase.execute(command);

    StepVerifier.create(resultMono)
        .assertNext(
            page -> {
              assertThat(page.total()).isZero();
              assertThat(page.values()).isEmpty();
            })
        .verifyComplete();

    verify(authGateway, never()).findUsersByIdIn(anySet());
    verify(productTypeRepository, never()).findById(any(UUID.class));
  }
}
