package com.midas.app.providers.payment;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UpdateAccount {
  private String providerAccountId;
  private String firstName;
  private String lastName;
  private String email;
}
