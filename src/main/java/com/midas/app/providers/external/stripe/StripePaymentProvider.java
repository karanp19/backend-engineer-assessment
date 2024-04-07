package com.midas.app.providers.external.stripe;

import com.midas.app.models.enums.Provider;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.providers.payment.UpdateAccount;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountUpdateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentProvider implements PaymentProvider {
  private final Logger logger = LoggerFactory.getLogger(StripePaymentProvider.class);
  @Autowired private StripeConfiguration configuration;

  @PostConstruct
  public void setApikey() {
    Stripe.apiKey = configuration.getApiKey();
  }

  /** providerName is the name of the payment provider */
  @Override
  public String providerName() {
    return Provider.STRIPE.name();
  }

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public String createAccount(CreateAccount details) throws StripeException {
    logger.info("Initiating createAccount() in StripePaymentProvider");
    AccountCreateParams params =
        AccountCreateParams.builder()
            .setEmail(details.getEmail())
            .setBusinessProfile(
                AccountCreateParams.BusinessProfile.builder()
                    .setName(String.format("%s %s", details.getFirstName(), details.getLastName()))
                    .build())
            .setType(AccountCreateParams.Type.STANDARD)
            .build();
    Account account = Account.create(params);
    logger.info("Exiting createAccount() in StripePaymentProvider");
    return account.getId();
  }

  /**
   * updateAccount updates an existing account in the payment provider.
   *
   * @param details is the details of the account to be updated.
   * @return ID of the provider account
   */
  @Override
  public String updateAccount(UpdateAccount details) throws StripeException {
    logger.info("Initiating updateAccount() in StripePaymentProvider");
    Account account = Account.retrieve(details.getProviderAccountId());
    AccountUpdateParams params =
        AccountUpdateParams.builder()
            .setEmail(details.getEmail())
            .setBusinessProfile(
                AccountUpdateParams.BusinessProfile.builder()
                    .setName(String.format("%s %s", details.getFirstName(), details.getLastName()))
                    .build())
            .build();
    account.update(params);
    logger.info("Exiting updateAccount() in StripePaymentProvider");
    return account.getId();
  }
}
