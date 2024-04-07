package com.midas.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.midas.app.providers.external.stripe.StripeConfiguration;
import com.midas.app.providers.external.stripe.StripePaymentProvider;
import com.midas.app.providers.payment.CreateAccount;
import com.stripe.Stripe;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StripePaymentProviderTest {
  @Mock StripeConfiguration configuration;
  @InjectMocks StripePaymentProvider paymentProvider = new StripePaymentProvider();
  String id;
  Account providerAccount;
  Account updatedProviderAccount;

  @Before
  public void before() {
    id = "acct_123";
    Stripe.apiKey =
        "sk_test_51OuRwiSDCpN0z6eUl7vwDrIihnzIIoj98PjLXlUqVU3oPxur4Pf0fEW5NshmnADJqyPA80o6gGimAmoQxKgiHp2R00hROQGaas";
    providerAccount = new Account();
    providerAccount.setId(id);
    providerAccount.setEmail("a@b.com");
    Account.BusinessProfile businessProfile = new Account.BusinessProfile();
    businessProfile.setName("abc xyz");
    providerAccount.setBusinessProfile(businessProfile);
    updatedProviderAccount = new Account();
    updatedProviderAccount.setId(id);
    updatedProviderAccount.setEmail("c@d.com");
    Account.BusinessProfile updatedBusinessProfile = new Account.BusinessProfile();
    updatedBusinessProfile.setName("def pqr");
    updatedProviderAccount.setBusinessProfile(updatedBusinessProfile);
  }

  @Test
  public void testCreateAccount() throws StripeException {
    MockedStatic<Account> acc = mockStatic(Account.class);
    acc.when(() -> Account.create(any(AccountCreateParams.class))).thenReturn(providerAccount);
    CreateAccount createAccount =
        CreateAccount.builder().firstName("abc").lastName("xyz").email("a@b.com").build();
    String providerAccountId = paymentProvider.createAccount(createAccount);
    assertEquals("acct_123", providerAccountId);
    acc.close();
  }

  @Test
  public void testCreateAccountThrowsStripeException() throws StripeException {
    MockedStatic<Account> acc = mockStatic(Account.class);
    acc.when(() -> Account.create(any(AccountCreateParams.class)))
        .thenThrow(
            new ApiException("connection failed", "requestId", "code", 500, new Throwable()));
    CreateAccount createAccount =
        CreateAccount.builder().firstName("abc").lastName("xyz").email("a@b.com").build();
    assertThrows(StripeException.class, () -> paymentProvider.createAccount(createAccount));
    acc.close();
  }
}
