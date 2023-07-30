package com.vladbstrv.customer;

import com.vladbstrv.exception.DuplicateResourceException;
import com.vladbstrv.exception.RequestValidationException;
import com.vladbstrv.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        long id = 10;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        long id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(
                        "customer with id [%s] not found".formatted(id));
    }


    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );
        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );
        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        //Given
        long id = 10L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        //Given
        long id = 10L;

        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gamil.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", newEmail, 23);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro", null, null);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gamil.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null, 2);

        //When
        underTest.updateCustomer(id, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@gamil.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        //Given
        long id = 10L;
        Customer customer = new Customer(
                id, "alex", "alex@gmail.com", 19
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge());

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        //Then

        verify(customerDao, never()).updateCustomer(any());
    }
}