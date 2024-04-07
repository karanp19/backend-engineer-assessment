package com.midas.app.activities.impl;

import com.midas.app.activities.UpdateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.providers.payment.UpdateAccount;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.UpdateAccountWorkflow;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ActivityImpl(taskQueues = UpdateAccountWorkflow.QUEUE_NAME)
public class UpdateAccountActivityImpl implements UpdateAccountActivity {
  private final Logger logger = Workflow.getLogger(UpdateAccountActivityImpl.class);
  @Autowired private AccountRepository accountRepository;
  @Autowired private PaymentProvider paymentProvider;

  @Override
  public Account saveAccount(Account account) {
    account = accountRepository.save(account);
    return account;
  }

  @Override
  public Optional<Account> getAccount(UUID accountId) {
    Optional<Account> accoutOpt = accountRepository.findById(accountId);
    return accoutOpt;
  }

  @Override
  public Account updatePaymentAccount(Account account) throws Exception {
    logger.info("Initiating updatePaymentAccount() in UpdateAccountActivityImpl");
    UpdateAccount updateAccount =
        UpdateAccount.builder()
            .providerAccountId(account.getProviderAccountId())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .email(account.getEmail())
            .build();
    String providerAccountId = paymentProvider.updateAccount(updateAccount);
    logger.info("Provider Account Id {} updated", providerAccountId);
    logger.info("Exiting updatePaymentAccount() in UpdateAccountActivityImpl");
    return account;
  }
}
