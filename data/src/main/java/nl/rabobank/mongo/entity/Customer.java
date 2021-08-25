package nl.rabobank.mongo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.Account;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customer")
public class Customer {

    @Id
    private String id;

    private String name;

    @Field(name = "accounts")
    private List<Account> accounts=new ArrayList<>();

    @Field(name = "power_of_attorney_list")
    private List<PowerOfAttorney> powerOfAttorneyList=new ArrayList<>();
}
