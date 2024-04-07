package com.midas.app.workflows.impl;

import com.midas.app.activities.CreateAccountActivity;
import com.midas.app.models.Account;
import com.midas.app.workflows.CreateAccountWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

@WorkflowImpl(taskQueues = CreateAccountWorkflow.QUEUE_NAME)
public class CreateAccountWorkflowImpl implements CreateAccountWorkflow {
  private final Logger logger = Workflow.getLogger(CreateAccountWorkflowImpl.class);
  private final RetryOptions retryoptions =
      RetryOptions.newBuilder()
          .setInitialInterval(Duration.ofSeconds(1))
          .setMaximumInterval(Duration.ofSeconds(100))
          .setBackoffCoefficient(2)
          .setMaximumAttempts(1)
          .build();
  private final ActivityOptions defaultActivityOptions =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(30))
          .setRetryOptions(retryoptions)
          .build();
  private final CreateAccountActivity activity =
      Workflow.newActivityStub(CreateAccountActivity.class, defaultActivityOptions);

  @Override
  public Account createAccount(Account details) throws Exception {
    logger.info("Initiating createAccount() in CreateAccountWorkFlowImpl");
    details = activity.createPaymentAccount(details);
    details = activity.saveAccount(details);
    logger.info("Exiting createAccount() in CreateAccountWorkFlowImpl");
    return details;
  }
}
