package nl.rabobank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountQueryResponse {
    List<String> readPermissions;
    List<String> writePermissions;

}
