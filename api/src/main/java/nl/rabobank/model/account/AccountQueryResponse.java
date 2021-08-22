package nl.rabobank.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryResponse {
    @JsonProperty("read_permissions")
    List<String> readPermissions;
    @JsonProperty("write_permissions")
    List<String> writePermissions;

}
