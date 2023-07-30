package com.vladbstrv.customer;

import java.util.List;
import java.util.Optional;


public interface CustomerDao {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    boolean existsCustomerWithId(Long customerId);
    void deleteCustomerById(Long customerId);
    void updateCustomer(Customer customer);
}
