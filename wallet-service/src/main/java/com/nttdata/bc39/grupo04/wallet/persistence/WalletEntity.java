package com.nttdata.bc39.grupo04.wallet.persistence;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "wallets")
public class WalletEntity {

	private ObjectId id;
    private String customerId;
    private String productId;
    private String phoneNumber;
    private String imei;
    private String email;
    private double availableBalance;
    private String debitCardNumber;
    private Date createDate;
    private Date modifyDate;

}
