package com.midas.app.providers.payment;

import com.stripe.exception.StripeException;

public interface PaymentProvider {
  /** providerName is the name of the payment provider */
  String providerName();

  /**
   * createAccount creates a new account in the payment provider.
   *
   * @param details is the details of the account to be created.
   * @return ID of the provider account
   */
  String createAccount(CreateAccount details) throws StripeException;

  /**
   * updateAccount updates an existing account in the payment provider.
   *
   * @param details is the details of the account to be updated.
   * @return ID of the provider account
   */
  String updateAccount(UpdateAccount details) throws StripeException;
}
