package com.nttdata.bc39.grupo04.customer.controller;

import com.nttdata.bc39.grupo04.api.customer.CustomerDTO;
import com.nttdata.bc39.grupo04.api.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("all")
@RequestMapping(value = "/customer")
public class CustomerController {
    @Autowired
    private CustomerService service;

    @GetMapping(value = "/all")
    List<CustomerDTO> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping(value = "/{customerId}")
    CustomerDTO getCustomerById(@PathVariable(value = "customerId") String customerId) {
        return service.getCustomerById(customerId);
    }

    @PostMapping(value = "/save")
    CustomerDTO createCustomer(@RequestBody CustomerDTO body) {
        return service.createCustomer(body);
    }

    @PutMapping(value = "/{customerId}")
    CustomerDTO updateCustomer(@PathVariable(value = "customerId") String customerId,
                               @RequestBody CustomerDTO body) {
        return service.updateCustomerById(customerId, body);
    }

    @DeleteMapping(value = "/{customerId}")
    void deleteCustomerByCode(@PathVariable(value = "customerId") String customerId) {
        service.deleteCustomerById(customerId);
    }
}
