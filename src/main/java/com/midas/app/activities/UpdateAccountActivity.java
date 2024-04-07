package com.midas.app.activities;

import com.midas.app.models.Account;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UpdateAccountActivity extends AccountActivity {
  /**
   * updatePaymentAccount updates a payment account in the system or provider.
   *
   * @param account is the account to be updates
   * @return Account
   */
  @ActivityMethod
  Account updatePaymentAccount(Account account) throws Exception;
}
