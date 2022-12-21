package com.nttdata.bc39.grupo04.api.bootcoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcoinDTO {
    private String documentNumber;
    private String documentType;
    private String phoneNumber;
    private String mail;
}
