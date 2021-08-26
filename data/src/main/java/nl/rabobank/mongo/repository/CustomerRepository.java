package nl.rabobank.mongo.repository;

import nl.rabobank.mongo.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findCustomerById(String customerId);

    List<Customer> findCustomersByIdIn(List<String> idList);

    Optional<Customer> findCustomersByName(String name);
}
