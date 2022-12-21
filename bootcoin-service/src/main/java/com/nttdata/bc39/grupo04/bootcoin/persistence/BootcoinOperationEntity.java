package com.nttdata.bc39.grupo04.bootcoin.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "operation")
public class BootcoinOperationEntity implements Serializable {
    @Id
    private ObjectId id;
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
