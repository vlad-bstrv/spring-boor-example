package com.vladbstrv.customer;

import java.util.List;
import java.util.Optional;


public interface CustomerDao {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    boolean existsPersonWithId(Integer customerId);
    void deleteCustomerById(Integer customerId);
    void updateCustomer(Customer customer);
}
