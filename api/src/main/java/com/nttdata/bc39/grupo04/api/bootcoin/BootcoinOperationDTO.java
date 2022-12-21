package com.nttdata.bc39.grupo04.api.bootcoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcoinOperationDTO implements Serializable {
    private String requestNumber;
    private String buyerNumber;
    private String payMethod;
    private String buyerNumberWallet;
    private String buyerAccountNumber;
    private String sellerNumberWallet;
    private String sellerAccountNumber;
    private int amountCoins;
    private double totalAmountPen;
    private String sellerNumber;
    private Date buyerRequestDate;
    private Date sellerAcceptanceDate;
    private String transactionNumber;
}
