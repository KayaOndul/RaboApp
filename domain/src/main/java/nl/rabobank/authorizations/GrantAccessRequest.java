package nl.rabobank.authorizations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantAccessRequest {

    @NotBlank(message = "INVALID_REQUEST_BODY")
    @JsonProperty("grantee_id")
    private String granteeId;

    @NotBlank(message = "INVALID_REQUEST_BODY")
    @JsonProperty("grantor_id")
    private String grantorId;

    @JsonProperty("authorization")
    private Authorization authorization;

    @NotBlank(message = "INVALID_REQUEST_BODY")
    @JsonProperty("account_number")
    private String accountNumber;
}
