package nl.rabobank.service.spec;

import nl.rabobank.account.AccountQueryResponse;
import nl.rabobank.authorizations.GrantAccessRequest;

public interface GrantService {

    AccountQueryResponse getGrantsOfUser(String granteeId);

    void grantAccessToUser(GrantAccessRequest grantAccessRequest);


}
