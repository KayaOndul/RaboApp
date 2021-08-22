package nl.rabobank.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nl.rabobank.authorizations.Authorization;

@Data
public class GrantAccessRequest {
    @JsonProperty("grantee_id")
    private String granteeId;
    @JsonProperty("grantor_id")
    private String grantorId;
    @JsonProperty("authorization")
    private Authorization authorization;
    @JsonProperty("account_number")
    private String accountNumber;
}
