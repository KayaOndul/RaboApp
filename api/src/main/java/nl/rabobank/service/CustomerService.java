package nl.rabobank.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.model.account.AccountQueryResponse;
import nl.rabobank.model.authorization.GrantAccessRequest;
import nl.rabobank.mongo.data.Customer;
import nl.rabobank.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public void grantAccessToUser(GrantAccessRequest grantAccessRequest) {
        Customer grantor = this.getCustomerById(grantAccessRequest.getGrantorId());
        Customer grantee = this.getCustomerById(grantAccessRequest.getGranteeId());

        Account account = getAccountByAccountNumber(grantor, grantAccessRequest);


        addPowerOfAttorneyToGrantee(grantAccessRequest, grantor, account, grantee);

        customerRepository.save(grantee);


    }

    public Customer getCustomerById(String grantorId) {
        return customerRepository.findCustomerById(grantorId).orElseThrow(
//                ()->new ApiException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public AccountQueryResponse getGrantsOfUser(String granteeId) {
        Customer grantee = getCustomerById(granteeId);

        var authorizationListMap =
                grantee.getPowerOfAttorneyList().stream().collect(Collectors.groupingBy(PowerOfAttorney::getAuthorization,
                        Collectors.mapping(elem->elem.getAccount().getAccountNumber(),Collectors.toList())));

        return new AccountQueryResponse(
               authorizationListMap.get(Authorization.READ),
                authorizationListMap.get(Authorization.WRITE));


    }

    private void addPowerOfAttorneyToGrantee(GrantAccessRequest grantAccessRequest, Customer grantor, Account account, Customer grantee) {
        grantee.getPowerOfAttorneyList().add(PowerOfAttorney.builder()
                .granteeName(grantee.getName())
                .grantorName(grantor.getName())
                .authorization(grantAccessRequest.getAuthorization())
                .account(account)
                .build());
    }

    private Account getAccountByAccountNumber(Customer customer, GrantAccessRequest grantAccessRequest) {

        var accountList = customer.getAccounts()
                .stream()
                .filter(account -> account.getAccountNumber().equals(grantAccessRequest.getAccountNumber()))
                .collect(Collectors.toList());

        if (accountList.isEmpty()) {
//            throw new ApiException(ErrorCode.NOT_GRANTORS_ACCOUNT)
            return null;//Todo throw error
        }
        return accountList.get(0);
    }

}
