package com.midas.app.services;

import com.midas.app.models.Account;
import com.midas.app.repositories.AccountRepository;
import com.midas.app.workflows.CreateAccountWorkflow;
import com.midas.app.workflows.UpdateAccountWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.workflow.Workflow;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
  private final Logger logger = Workflow.getLogger(AccountServiceImpl.class);
  @Autowired private WorkflowClient workflowClient;
  @Autowired private AccountRepository accountRepository;

  /**
   * createAccount creates a new account in the system or provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account createAccount(Account details) throws Exception {
    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(CreateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(details.getEmail())
            .build();
    logger.info("initiating workflow to create account for email: {}", details.getEmail());
    CreateAccountWorkflow workflow =
        workflowClient.newWorkflowStub(CreateAccountWorkflow.class, options);
    return workflow.createAccount(details);
  }

  /**
   * getAccounts returns a list of accounts.
   *
   * @return List<Account>
   */
  @Override
  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }

  /**
   * updateAccount updates an existing account in the system or provider.
   *
   * @param details is the details of the account to be created.
   * @return Account
   */
  @Override
  public Account updateAccount(Account details) throws Exception {
    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(UpdateAccountWorkflow.QUEUE_NAME)
            .setWorkflowId(details.getId().toString())
            .build();
    logger.info("initiating workflow to update account for id: {}", details.getId());
    UpdateAccountWorkflow workflow =
        workflowClient.newWorkflowStub(UpdateAccountWorkflow.class, options);
    return workflow.updateAccount(details);
  }
}
