package com.midas.app;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.midas.app.activities.UpdateAccountActivity;
import com.midas.app.activities.impl.UpdateAccountActivityImpl;
import com.midas.app.models.Account;
import com.midas.app.models.enums.Provider;
import com.midas.app.providers.payment.PaymentProvider;
import com.midas.app.repositories.AccountRepository;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class UpdateAccountActivityTest {
  @Mock private AccountRepository accountRepository;
  @Mock private PaymentProvider paymentProvider;
  @InjectMocks UpdateAccountActivity activity = new UpdateAccountActivityImpl();

  UUID id;
  Account account;

  @Before
  public void getInitialData() {
    id = UUID.randomUUID();
    String providerAccountId = "acct_123";
    String firstName = "def";
    String lastName = "pqr";
    Provider provider = Provider.STRIPE;
    account =
        Account.builder()
            .id(id)
            .firstName(firstName)
            .lastName(lastName)
            .provider(provider)
            .providerAccountId(providerAccountId)
            .build();
  }

  @Test
  public void testGetAccount() {
    when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    Optional<Account> actualAccountOpt = activity.getAccount(id);
    assertTrue(actualAccountOpt.isPresent());
    Account actualAccount = actualAccountOpt.get();
    assertEquals(id, actualAccount.getId());
    assertEquals("def", actualAccount.getFirstName());
    assertEquals("pqr", actualAccount.getLastName());
    assertEquals(Provider.STRIPE, actualAccount.getProvider());
    assertEquals("acct_123", actualAccount.getProviderAccountId());
  }

  @Test
  public void testGetAccountThrowsException() {
    when(accountRepository.findById(id)).thenThrow(new RuntimeException("connection exception"));
    assertThrows(RuntimeException.class, () -> activity.getAccount(id));
  }

  @Test
  public void testSaveAccount() {
    when(accountRepository.save(account)).thenReturn(account);
    Account actualAccount = activity.saveAccount(account);
    assertEquals(id, actualAccount.getId());
    assertEquals("def", actualAccount.getFirstName());
    assertEquals("pqr", actualAccount.getLastName());
    assertEquals(Provider.STRIPE, actualAccount.getProvider());
    assertEquals("acct_123", actualAccount.getProviderAccountId());
  }

  @Test
  public void testSaveAccountThrowsException() {
    when(accountRepository.save(account)).thenThrow(new RuntimeException("connection exception"));
    assertThrows(RuntimeException.class, () -> activity.saveAccount(account));
  }

  @Test
  public void testUpdatePaymentAccount() throws Exception {
    when(paymentProvider.updateAccount(any())).thenReturn("acct_123");
    Account actualUpdatedAccount = activity.updatePaymentAccount(account);
    assertEquals(id, actualUpdatedAccount.getId());
    assertEquals("def", actualUpdatedAccount.getFirstName());
    assertEquals("pqr", actualUpdatedAccount.getLastName());
    assertEquals(Provider.STRIPE, actualUpdatedAccount.getProvider());
    assertEquals("acct_123", actualUpdatedAccount.getProviderAccountId());
  }

  @Test
  public void testUpdatePaymentAccountThrowsException() throws Exception {
    when(paymentProvider.updateAccount(any()))
        .thenThrow(
            new ApiException("account updation failed", "requestId", "code", 500, new Throwable()));
    assertThrows(StripeException.class, () -> activity.updatePaymentAccount(account));
  }
}
