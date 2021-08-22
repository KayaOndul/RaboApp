package nl.rabobank.mongo.data;


import lombok.Data;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "user")
public class Customer {

    @Id
    private Long id;

    private String name;

    private List<Account> accounts;

    private List<PowerOfAttorney> powerOfAttorneyList;
}
