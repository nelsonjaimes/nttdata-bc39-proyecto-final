package com.nttdata.bc39.grupo04.api.wallet;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
public class WalletAssociatedDTO implements Serializable {
    private String customerId;
    private String debitCardNumber;
    private String phoneNumber;
}
