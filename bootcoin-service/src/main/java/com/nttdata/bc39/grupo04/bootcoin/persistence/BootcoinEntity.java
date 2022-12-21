package com.nttdata.bc39.grupo04.bootcoin.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bootcoin")
public class BootcoinEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String documentNumber;
    private String typeUser;
    private String documentType;
    private String phoneNumber;
    private String mail;
    private double amountCoins;
    private Date createdDate;

}