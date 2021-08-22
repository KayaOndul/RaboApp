package nl.rabobank.repository;

import nl.rabobank.mongo.data.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

    Optional<Customer> findCustomerById(String customerId);
}
