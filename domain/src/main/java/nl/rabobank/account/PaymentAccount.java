package nl.rabobank.account;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PaymentAccount implements Account
{
    String accountNumber;
    String accountHolderName;
    Double balance;
}
