package nl.rabobank.service.impl;

import nl.rabobank.error.ErrorCode;
import nl.rabobank.exception.ApiException;
import nl.rabobank.mongo.entity.Customer;
import nl.rabobank.mongo.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabobankCustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    RabobankCustomerService customerService;

    public static final String CUSTOMER_ID_ONE = "2897163a01280398";
    public static final String CUSTOMER_ID_TWO = "213322112321a32";
    public static final String NAME_ONE = "John Doe";

    @Test
    void shouldThrowExceptionIfNoUsersFound() {
        when(customerRepository.findCustomersByIdIn(any())).thenReturn(Collections.emptyList());

        ApiException actual = assertThrows(ApiException.class, () -> customerService.getCustomersByIdIn(any()));
        assertEquals(ErrorCode.NO_USERS_FOUND, actual.getErrorCode());
    }

    @Test
    void shouldReturnCustomerListIfNotEmpty() {
        when(customerRepository.findCustomersByIdIn(any())).thenReturn(List.of(Customer.builder().name(NAME_ONE).build()));

        var actual = customerService.getCustomersByIdIn(List.of(CUSTOMER_ID_ONE, CUSTOMER_ID_TWO));
        assertEquals(NAME_ONE, actual.get(0).getName());
    }

    @Test
    void shouldCallRepositorySaveMethod() {
        customerService.save(new Customer());
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowErrorIfCustomerNotFound() {
        when(customerRepository.findCustomerById(CUSTOMER_ID_ONE)).thenReturn(Optional.empty());

        ApiException actual = assertThrows(ApiException.class, () -> customerService.getCustomerById(CUSTOMER_ID_ONE));
        assertEquals(ErrorCode.USER_NOT_FOUND, actual.getErrorCode());

    }

    @Test
    void shouldReturnCustomerIfFound() {
        when(customerRepository.findCustomerById(CUSTOMER_ID_ONE)).thenReturn(Optional.of(Customer.builder().name(NAME_ONE).build()));
        Customer actual = customerService.getCustomerById(CUSTOMER_ID_ONE);
        assertEquals(actual.getName(), NAME_ONE);
    }
}