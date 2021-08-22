package nl.rabobank.mongo.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "user")
public class Customer extends Base {

    private String name;

    private List<Account> accounts;

    private List<PowerOfAttorney> powerOfAttorneyList;
}
