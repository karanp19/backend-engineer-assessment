package com.midas.app.activities;

import com.midas.app.models.Account;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CreateAccountActivity extends AccountActivity {
  /**
   * createPaymentAccount creates a payment account in the system or provider.
   *
   * @param account is the account to be created
   * @return Account
   */
  @ActivityMethod
  Account createPaymentAccount(Account account) throws Exception;
}
