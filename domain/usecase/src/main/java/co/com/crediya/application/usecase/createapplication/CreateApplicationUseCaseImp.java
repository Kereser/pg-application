package co.com.crediya.application.usecase.createapplication;

import java.math.BigDecimal;
import java.util.UUID;

import co.com.crediya.application.model.application.Application;
import co.com.crediya.application.model.application.gateways.ApplicationRepository;
import co.com.crediya.application.model.application.vo.Amount;
import co.com.crediya.application.model.application.vo.IdNumber;
import co.com.crediya.application.model.applicationstatus.ApplicationStatus;
import co.com.crediya.application.model.applicationstatus.ApplicationStatusName;
import co.com.crediya.application.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.crediya.application.model.auth.UserSummary;
import co.com.crediya.application.model.auth.gateway.AuthGateway;
import co.com.crediya.application.model.dto.ApplicationDTOResponse;
import co.com.crediya.application.model.dto.CreateApplicationCommand;
import co.com.crediya.application.model.exceptions.*;
import co.com.crediya.application.model.mapper.ApplicationMapper;
import co.com.crediya.application.model.producttype.ProductType;
import co.com.crediya.application.model.producttype.gateways.ProductTypeRepository;
import co.com.crediya.application.model.producttype.vo.ProductName;
import co.com.crediya.application.model.security.SecurityGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateApplicationUseCaseImp implements CreateApplicationUseCase {

  private final ApplicationRepository applicationRepository;
  private final ProductTypeRepository productTypeRepository;
  private final ApplicationStatusRepository appStatusRepository;
  private final SecurityGateway securityGateway;
  private final ApplicationMapper mapper;
  private final AuthGateway authClient;

  @Override
  public Mono<ApplicationDTOResponse> execute(CreateApplicationCommand command) {
    IdNumber idNumber = new IdNumber(command.idNumber());
    ProductName productName = new ProductName(command.productName());

    Application application = mapper.toEntityCommand(command);

    return Mono.zip(findUser(idNumber), findProduct(productName), findPending())
        .flatMap(
            tuple ->
                validateResourceOwnership(tuple.getT1().id())
                    .then(
                        Mono.defer(
                            () -> {
                              runAmountValidation(tuple.getT2(), application);

                              return Mono.just(
                                  this.buildApplication(
                                      tuple.getT1(), tuple.getT2(), tuple.getT3(), application));
                            })))
        .flatMap(applicationRepository::save)
        .map(mapper::toDTO);
  }

  private Mono<UserSummary> findUser(IdNumber idNumber) {
    return authClient.findUserByIdNumber(idNumber.value());
  }

  private Mono<Void> validateResourceOwnership(UUID reqUserId) {
    return this.securityGateway
        .getDetailsFromContext()
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    Entities.SECURITY_CONTEXT.name(), PlainErrors.NOT_EMPY.getName())))
        .flatMap(
            usrDetails ->
                usrDetails.userId().equals(reqUserId)
                    ? Mono.empty()
                    : Mono.error(
                        new ResourceOwnershipException(
                            Fields.USER_ID.getName(), PlainErrors.OWNERSHIP.getName())));
  }

  private Mono<ProductType> findProduct(ProductName productName) {
    return productTypeRepository
        .findByName(productName.value())
        .switchIfEmpty(
            Mono.error(
                new IllegalValueForArgumentException(
                    Fields.PRODUCT_NAME.getName(),
                    TemplateErrors.X_NOT_FOUND_FOR_Y.buildMsg(
                        Fields.PRODUCT_NAME.getName(), productName))));
  }

  private Mono<ApplicationStatus> findPending() {
    return appStatusRepository.findByName(ApplicationStatusName.PENDING);
  }

  private void runAmountValidation(ProductType productType, Application application) {
    BigDecimal requestedAmount = application.getAmount().value();

    if (requestedAmount.compareTo(productType.getMinAmount()) < 0
        || requestedAmount.compareTo(productType.getMaxAmount()) > 0) {

      String amountStr = Amount.class.getSimpleName();
      throw new IllegalValueForArgumentException(
          amountStr,
          TemplateErrors.X_NOT_VALID_VALUE_FOR_Y_WITH_RANGE.buildMsg(
              requestedAmount.toPlainString(),
              amountStr,
              productType.getMinAmount().toPlainString(),
              productType.getMaxAmount().toPlainString()));
    }
  }

  private Application buildApplication(
      UserSummary usr, ProductType productType, ApplicationStatus status, Application application) {
    return application.toBuilder()
        .userId(usr.id())
        .productType(productType)
        .applicationStatus(status)
        .build();
  }
}
