package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.GrantAccessRequest;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.entity.Customer;
import nl.rabobank.mongo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(EmbeddedMongoAutoConfiguration.class)
@PropertySource(value = "classpath:application.properties")

class GrantControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        customerRepository.deleteAll();
        PaymentAccount paymentAccountOne = PaymentAccount.builder()
                .accountNumber("1234561")
                .accountHolderName("Kaya Ondul")
                .balance(100.00)
                .build();

        PaymentAccount paymentAccountTwo = PaymentAccount.builder()
                .accountNumber("1234562")
                .accountHolderName("Busra Ondul")
                .balance(100.00)
                .build();

        PaymentAccount paymentAccountThree = PaymentAccount.builder()
                .accountNumber("1234563")
                .accountHolderName("Sinan Ondul")
                .balance(100.00)
                .build();

        SavingsAccount savingsAccountOne = SavingsAccount.builder()
                .accountNumber("1234564")
                .accountHolderName("Kaya Ondul")
                .balance(100.00)
                .build();

        PowerOfAttorney powerOfAttorneyOne = PowerOfAttorney.builder()
                .account(paymentAccountOne)
                .authorization(Authorization.READ)
                .granteeName("Sinan Ondul")
                .grantorName("Kaya Ondul").build();

        PowerOfAttorney powerOfAttorneyTwo = PowerOfAttorney.builder()
                .account(savingsAccountOne)
                .authorization(Authorization.WRITE)
                .granteeName("Sinan Ondul")
                .grantorName("Kaya Ondul").build();

        PowerOfAttorney powerOfAttorneyThree = PowerOfAttorney.builder()
                .account(paymentAccountTwo)
                .authorization(Authorization.WRITE)
                .granteeName("Sinan Ondul")
                .grantorName("Busra Ondul").build();


        Customer customerOne = Customer.builder()
                .name("Kaya Ondul")
                .accounts(List.of(paymentAccountOne, savingsAccountOne))
                .build();

        Customer customerTwo = Customer.builder()
                .name("Busra Ondul").accounts(List.of(paymentAccountTwo)).build();


        Customer customerThree = Customer.builder()
                .name("Sinan Ondul")
                .powerOfAttorneyList(List.of(powerOfAttorneyOne, powerOfAttorneyTwo, powerOfAttorneyThree))
                .accounts(List.of(paymentAccountThree))
                .build();


        customerRepository.saveAll(List.of(customerOne, customerTwo, customerThree));
    }

    @Test
    void shouldReturnWriteAndReadPermissionsSuccessfully() throws Exception {

        var thirdCustomer = customerRepository.findCustomersByName("Sinan Ondul").get();
        mvc.perform(
                MockMvcRequestBuilders.get("/grant")
                        .param("granteeId", thirdCustomer.getId())
        ).andExpect(
                status().isOk()
        ).andExpect(content().string("{\"read_permissions\":[\"1234561\"],\"write_permissions\":[\"1234564\",\"1234562\"]}")
        );
    }

    @Test
    void shouldAddGrantToSecondCustomerSuccessfully() throws Exception {
        var second = customerRepository.findCustomersByName("Busra Ondul").get();
        var first = customerRepository.findCustomersByName("Kaya Ondul").get();

        GrantAccessRequest grantAccessRequest = GrantAccessRequest.builder()
                .grantorId(first.getId())
                .granteeId(second.getId())
                .accountNumber(first.getAccounts().get(0).getAccountNumber())
                .authorization(Authorization.WRITE)
                .build();

        var jsonString = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(grantAccessRequest);

        mvc.perform(
                MockMvcRequestBuilders.post("/grant")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isAccepted()
        );
    }

    @Test
    void shouldNotAcceptBlankValuesInGrantAcceptRequest() throws Exception {

        GrantAccessRequest grantAccessRequest = GrantAccessRequest.builder()
                .grantorId("")
                .granteeId("12415")
                .accountNumber("123123")
                .authorization(Authorization.WRITE)
                .build();

        var jsonString = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(grantAccessRequest);

        mvc.perform(
                MockMvcRequestBuilders.post("/grant")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().isBadRequest()
        )
        .andExpect(content().string("{\"errors\":[{\"error_code\":\"INVALID_REQUEST_BODY\",\"message\":\"Invalid Request. Please check your request.\",\"argument\":\"grantorId\"}]}"));
    }
}
