package com.nttdata.bc39.grupo04.api.customer;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(String customerId);

    void deleteCustomerById(String customerId);

    CustomerDTO createCustomer(CustomerDTO customerDto);

    CustomerDTO updateCustomerById(String customerId, CustomerDTO customerDto);
}
