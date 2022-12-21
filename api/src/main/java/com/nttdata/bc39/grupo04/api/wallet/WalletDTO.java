package com.nttdata.bc39.grupo04.api.wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletDTO implements Serializable {

	private static final long serialVersionUID = -4315790585316570198L;
	private String customerId;
	
    private String productId;
	
    @JsonProperty("phoneNumber")
    @NotBlank(message = "Error, Número de teléfono de wallet inválido")
	private String phoneNumber;
    
    @JsonProperty("imei")
    @NotBlank(message = "Error, imei de wallet inválido")
	private String imei;
    
    @JsonProperty("email")
    @NotBlank(message = "Error, email de wallet inválido")
	private String email;
    
    private double availableBalance;
    
    private String debitCardNumber;
    
	private Date createDate;
	
	private Date modifyDate;

	public static WalletDTO of(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		WalletDTO dto = mapper.convertValue(object, WalletDTO.class);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<WalletDTO>> violations = validator.validate(dto);
		if (!CollectionUtils.isEmpty(violations)) {
			throw new InvaliteInputException(violations.iterator().next().getMessage());
		}
		return dto;
	}
}
