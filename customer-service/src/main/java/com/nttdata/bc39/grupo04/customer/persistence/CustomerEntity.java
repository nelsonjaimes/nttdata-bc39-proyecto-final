package com.nttdata.bc39.grupo04.customer.persistence;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.bc39.grupo04.api.utils.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerEntity extends BaseResponse implements Serializable {

	private static final long serialVersionUID = 2461118311570908533L;
	@Id
	private String id;
	@Indexed(unique = true)
	private String code;
	private String name;
	private String type;
	private Date date;
	private String documentType;
}
