package nl.rabobank.model;

import lombok.Data;
import nl.rabobank.authorizations.Authorization;

@Data
public class GrantAccessRequest {
    private Long granteeId;
    private Long grantorId;
    private Authorization authorization;
    private String accountNumber;
}
