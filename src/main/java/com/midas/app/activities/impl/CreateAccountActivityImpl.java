package com.midas.app.activities.impl;

import com.midas.app.activities.CreateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.providers.payment.CreateAccount;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.spring.boot.ActivityImpl;
import io.temporal.workflow.Workflow;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ActivityImpl(taskQueues = CreateAccountWorkflow.QUEUE_NAME)
public class CreateAccountActivityImpl implements CreateAccountActivity {
  private final Logger logger = Workflow.getLogger(CreateAccountActivityImpl.class);
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
  public Account createPaymentAccount(Account account) throws Exception {
    logger.info("Initiating createPaymentAccount() in CreateAccountActivityImpl");
    CreateAccount createAccount =
        CreateAccount.builder()
            .userId(account.getId().toString())
            .firstName(account.getFirstName())
            .lastName(account.getLastName())
            .email(account.getEmail())
            .build();
    String providerAccountId = paymentProvider.createAccount(createAccount);
    account.setProviderAccountId(providerAccountId);
    logger.info("Exiting createPaymentAccount() in CreateAccountActivityImpl");
    return account;
  }
}
