package nl.rabobank.service.impl;

import nl.rabobank.account.Account;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.GrantAccessRequest;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.error.ErrorCode;
import nl.rabobank.exception.ApiException;
import nl.rabobank.mongo.entity.Customer;
import nl.rabobank.service.spec.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static nl.rabobank.authorizations.Authorization.READ;
import static nl.rabobank.authorizations.Authorization.WRITE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabobankGrantServiceTest {
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;

    @Mock
    CustomerService customerService;

    @InjectMocks
    RabobankGrantService grantService;


    public static final Authorization AUTHORIZATION = READ;
    public static final String ACCOUNT_NUMBER = "12a123fc11";
    public static final String ACCOUNT_NUMBER_TWO = "12e123fc00";
    public static final String CUSTOMER_ID_ONE = "2897163a01280398";
    public static final String NAME_ONE = "John Doe";
    public static final String CUSTOMER_ID_TWO = "213322112321a32";
    public static final String NAME_TWO = "Jane Doe";

    static GrantAccessRequest grantAccessRequest;
    static Customer customerOne;
    static Customer customerTwo;
    static Account paymentAccountOne;
    static Account paymentAccountTwo;

    static List<Account> accountList;
    static List<PowerOfAttorney> powerOfAttorneyList;
    static PowerOfAttorney powerOfAttorney;
    static PowerOfAttorney powerOfAttorneyTwo;

    @BeforeEach
    public void init() {
        grantAccessRequest = GrantAccessRequest.builder()
                .authorization(AUTHORIZATION)
                .accountNumber(ACCOUNT_NUMBER_TWO)
                .granteeId(CUSTOMER_ID_TWO)
                .grantorId(CUSTOMER_ID_ONE)
                .build();


        paymentAccountOne = PaymentAccount.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountHolderName(NAME_ONE)
                .balance(100.00)
                .build();
        paymentAccountTwo = PaymentAccount.builder()
                .accountNumber(ACCOUNT_NUMBER_TWO)
                .accountHolderName(NAME_ONE)
                .balance(80.00)
                .build();

        accountList = List.of(paymentAccountOne, paymentAccountTwo);

        powerOfAttorney = PowerOfAttorney.builder()
                .account(paymentAccountOne)
                .grantorName(NAME_ONE)
                .granteeName(NAME_TWO)
                .authorization(WRITE)
                .build();

        powerOfAttorneyTwo = PowerOfAttorney.builder()
                .account(paymentAccountTwo)
                .grantorName(NAME_ONE)
                .granteeName(NAME_TWO)
                .authorization(READ)
                .build();

        powerOfAttorneyList = new ArrayList<>(List.of(powerOfAttorney, powerOfAttorneyTwo));

        customerTwo = Customer.builder()
                .accounts(new ArrayList<>())
                .id(CUSTOMER_ID_TWO)
                .name(NAME_TWO)
                .powerOfAttorneyList(powerOfAttorneyList)
                .build();

        customerOne = Customer.builder()
                .id(CUSTOMER_ID_ONE)
                .accounts(accountList)
                .powerOfAttorneyList(new ArrayList<>())
                .name(NAME_ONE)
                .build();
    }

    @Test
    void shouldThrowExceptionIfNotTwoRows() {
        when(customerService.getCustomersByIdIn(List.of(CUSTOMER_ID_ONE, CUSTOMER_ID_TWO))).thenReturn(List.of(customerOne));

        ApiException actual = assertThrows(ApiException.class, () -> grantService.grantAccessToUser(grantAccessRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND, actual.getErrorCode());
    }

    @Test
    void shouldThrowExceptionIfNotGrantorsAccount() {
        customerOne.setAccounts(new ArrayList<>());

        when(customerService.getCustomersByIdIn(List.of(CUSTOMER_ID_ONE, CUSTOMER_ID_TWO))).thenReturn(List.of(customerOne, customerTwo));

        ApiException actual = assertThrows(ApiException.class, () -> grantService.grantAccessToUser(grantAccessRequest));
        assertEquals(ErrorCode.NOT_GRANTORS_ACCOUNT, actual.getErrorCode());
    }

    @Test
    void shouldAddPowerOfAttorneyToGranteeBeforeSave() {

        when(customerService.getCustomersByIdIn(List.of(CUSTOMER_ID_ONE, CUSTOMER_ID_TWO))).thenReturn(List.of(customerOne, customerTwo));

        grantService.grantAccessToUser(grantAccessRequest);

        verify(customerService, times(1)).save(customerArgumentCaptor.capture());
        var actual = customerArgumentCaptor.getAllValues().get(0);
        assertTrue(actual.getPowerOfAttorneyList().stream().anyMatch(powerOfAttorney -> powerOfAttorney.getAccount().equals(paymentAccountTwo)));
    }

    @Test
    void shouldCallAllDependantMethods() {

        when(customerService.getCustomersByIdIn(List.of(CUSTOMER_ID_ONE, CUSTOMER_ID_TWO))).thenReturn(List.of(customerOne, customerTwo));

        grantService.grantAccessToUser(grantAccessRequest);

        verify(customerService, times(1)).getCustomersByIdIn(anyList());
        verify(customerService, times(1)).save(any());
    }


    @Test
    void shouldCallGetCustomerById() {
        when(customerService.getCustomerById(CUSTOMER_ID_TWO)).thenReturn(customerTwo);
        var actual = grantService.getGrantsOfUser(CUSTOMER_ID_TWO);
        assertTrue(actual.getWritePermissions().contains(ACCOUNT_NUMBER));
        assertTrue(actual.getReadPermissions().contains(ACCOUNT_NUMBER_TWO));
    }


}