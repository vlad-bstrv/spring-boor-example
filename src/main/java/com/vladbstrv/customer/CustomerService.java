package com.vladbstrv.customer;

import com.vladbstrv.exception.DuplicateResourceException;
import com.vladbstrv.exception.RequestValidationException;
import com.vladbstrv.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "customer with id [%s] not found".formatted(id)
                        ));
    }

    public void addCustomer(
            CustomerRegistrationRequest customerRegistrationRequest
    ) {
        String email = customerRegistrationRequest.email();
        if (customerDao.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException(
                    "email already taken");
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        if(!customerDao.existsPersonWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }

        customerDao.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
//            customerDao.insertCustomer(customer);
            changes = true;
        }

        if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if(customerDao.existsPersonWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("no data changes found");
        }

        customerDao.updateCustomer(customer);
    }
}
