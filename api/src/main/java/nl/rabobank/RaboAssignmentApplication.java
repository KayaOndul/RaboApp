package nl.rabobank;

import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.mongo.MongoConfiguration;
import nl.rabobank.mongo.data.Customer;
import nl.rabobank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"nl.rabobank.*"})
@EntityScan(basePackages = {"nl.rabobank.*"})
@EnableMongoRepositories(basePackages = {"nl.rabobank.*"})
@Import(MongoConfiguration.class)
public class RaboAssignmentApplication implements CommandLineRunner {
    public static void main(final String[] args) {
        SpringApplication.run(RaboAssignmentApplication.class, args);
    }


    @Autowired
    CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
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
                .powerOfAttorneyList(List.of(powerOfAttorneyOne,powerOfAttorneyTwo,powerOfAttorneyThree))
                .accounts(List.of(paymentAccountThree))
                .build();


        customerRepository.saveAll(List.of(customerOne, customerTwo, customerThree));


    }
}
