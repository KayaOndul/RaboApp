package nl.rabobank.service.impl;


import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.account.AccountQueryResponse;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.GrantAccessRequest;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.error.ErrorCode;
import nl.rabobank.exception.ApiException;
import nl.rabobank.mongo.entity.Customer;
import nl.rabobank.service.spec.CustomerService;
import nl.rabobank.service.spec.GrantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RabobankGrantService implements GrantService {
    private final CustomerService customerService;


    public AccountQueryResponse getGrantsOfUser(String granteeId) {
        final Customer grantee = customerService.getCustomerById(granteeId);

        final Map<Authorization, List<String>> authorizationListMap =
                grantee.getPowerOfAttorneyList().stream().collect(Collectors.groupingBy(PowerOfAttorney::getAuthorization,
                        Collectors.mapping(elem -> elem.getAccount().getAccountNumber(), Collectors.toList())));

        return new AccountQueryResponse(
                authorizationListMap.get(Authorization.READ),
                authorizationListMap.get(Authorization.WRITE));


    }
    @Transactional
    public void grantAccessToUser(GrantAccessRequest grantAccessRequest) {

        final List<Customer> customers = getGrantorAndGrantee(grantAccessRequest);

        final Customer grantor = getCustomerByIdFromCustomerList(grantAccessRequest.getGrantorId(), customers);
        Customer grantee = getCustomerByIdFromCustomerList(grantAccessRequest.getGranteeId(), customers);

        final Account account = getAccountByAccountNumber(grantor, grantAccessRequest);
        this.addPowerOfAttorneyToGrantee(grantAccessRequest, grantor, account, grantee);

        customerService.save(grantee);
    }

    private void addPowerOfAttorneyToGrantee(GrantAccessRequest grantAccessRequest, Customer grantor, Account account, Customer grantee) {
        checkGranteeHasGrant(account, grantee);

        grantee.getPowerOfAttorneyList().add(PowerOfAttorney.builder()
                .granteeName(grantee.getName())
                .grantorName(grantor.getName())
                .authorization(grantAccessRequest.getAuthorization())
                .account(account)
                .build());
    }

    private void checkGranteeHasGrant(Account account, Customer grantee) {
        boolean isAlreadyAssigned = grantee.getPowerOfAttorneyList()
                .stream().map(powerOfAttorney -> powerOfAttorney.getAccount().getAccountNumber())
                .anyMatch(accountNumber -> account.getAccountNumber().equals(accountNumber));
        if (isAlreadyAssigned) {
            throw new ApiException(ErrorCode.ALREADY_HAS_GRANT);
        }
    }

    private Account getAccountByAccountNumber(Customer customer, GrantAccessRequest grantAccessRequest) {

        final List<Account> accountList = customer.getAccounts()
                .stream()
                .filter(account -> account.getAccountNumber().equals(grantAccessRequest.getAccountNumber()))
                .collect(Collectors.toList());

        if (accountList.isEmpty()) {
            throw new ApiException(ErrorCode.NOT_GRANTORS_ACCOUNT);
        }
        return accountList.get(0);
    }

    private List<Customer> getGrantorAndGrantee(GrantAccessRequest grantAccessRequest) {
        final var customers = customerService.getCustomersByIdIn(List.of(grantAccessRequest.getGrantorId(), grantAccessRequest.getGranteeId()));
        if (customers.size() < 2) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);

        }
        return customers;
    }

    private Customer getCustomerByIdFromCustomerList(String id, List<Customer> customers) {

        return customers.stream().filter(customer -> customer.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
