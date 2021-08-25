package nl.rabobank.authorizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
