package com.nttdata.bc39.grupo04.api.bootcoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcoinDTO implements Serializable {
    private String documentNumber;
    private String documentType;
    private String phoneNumber;
    private String mail;
    private double amountCoins;
    private Date createdDate;
}
