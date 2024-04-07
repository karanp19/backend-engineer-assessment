package com.midas.app.activities;

import com.midas.app.models.Account;
import io.temporal.activity.ActivityMethod;
import java.util.Optional;
import java.util.UUID;

public interface AccountActivity {
  /**
   * saveAccount saves an account in the data store.
   *
   * @param account is the account to be saved
   * @return Account
   */
  @ActivityMethod
  Account saveAccount(Account account);

  /**
   * getAccount fetches an account in the data store.
   *
   * @param accountId is the accountId to be fetched
   * @return Optional<Account>
   */
  @ActivityMethod
  Optional<Account> getAccount(UUID accountId);
}
