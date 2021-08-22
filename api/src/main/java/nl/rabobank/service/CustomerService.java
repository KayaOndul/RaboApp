package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.model.AccountQueryResponse;
import nl.rabobank.model.GrantAccessRequest;
import nl.rabobank.mongo.data.Customer;
import nl.rabobank.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public void grantAccessToUser(GrantAccessRequest grantAccessRequest) {
        Customer grantor = customerRepository.findCustomerById(grantAccessRequest.getGrantorId()).orElseThrow();
        Account account = getGivenAccount(grantor, grantAccessRequest);

        Customer grantee = customerRepository.findCustomerById(grantAccessRequest.getGranteeId()).orElseThrow();

        addPowerOfAttorneyToGrantee(grantAccessRequest, grantor, account, grantee);

        customerRepository.save(grantee);


    }

    public AccountQueryResponse getGrantsOfUser(Long granteeId) {
        Customer grantee = customerRepository.findCustomerById(granteeId).orElseThrow();

        var authorizationListMap =
                grantee.getPowerOfAttorneyList().stream().collect(Collectors.groupingBy(PowerOfAttorney::getAuthorization));
        return new AccountQueryResponse(
                authorizationListMap.get(Authorization.READ).stream().map(powerOfAttorney -> powerOfAttorney.getAccount().getAccountNumber()).collect(Collectors.toList()),
                authorizationListMap.get(Authorization.READ).stream().map(powerOfAttorney -> powerOfAttorney.getAccount().getAccountNumber()).collect(Collectors.toList())
        );


    }

    private void addPowerOfAttorneyToGrantee(GrantAccessRequest grantAccessRequest, Customer grantor, Account account, Customer grantee) {
        grantee.getPowerOfAttorneyList().add(PowerOfAttorney.builder()
                .granteeName(grantee.getName())
                .grantorName(grantor.getName())
                .authorization(grantAccessRequest.getAuthorization())
                .account(account)
                .build());
    }

    private Account getGivenAccount(Customer customer, GrantAccessRequest grantAccessRequest) {

        var accountList = customer.getAccounts().stream().filter(account -> account.getAccountNumber().equals(grantAccessRequest.getAccountNumber())).collect(Collectors.toList());
        if (accountList.isEmpty()) {
            return null;//Todo throw error
        }
        return accountList.get(0);
    }
}
