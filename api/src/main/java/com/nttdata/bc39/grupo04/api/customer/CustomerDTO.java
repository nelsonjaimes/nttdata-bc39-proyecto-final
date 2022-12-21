package com.nttdata.bc39.grupo04.api.customer;

import com.nttdata.bc39.grupo04.api.utils.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDTO extends BaseResponse implements Serializable {
    private String code;
    private String name;
    private String type;
    private Date date;
}
