package nl.rabobank.service.spec;

import nl.rabobank.mongo.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> getCustomersByIdIn(List<String> idList);

    void save(Customer grantee);

    Customer getCustomerById(String granteeId);
}
