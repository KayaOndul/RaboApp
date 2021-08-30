package nl.rabobank.service.impl;

import lombok.RequiredArgsConstructor;
import nl.rabobank.error.ErrorCode;
import nl.rabobank.exception.ApiException;
import nl.rabobank.mongo.entity.Customer;
import nl.rabobank.mongo.repository.CustomerRepository;
import nl.rabobank.service.spec.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RabobankCustomerService implements CustomerService {
    private final CustomerRepository customerRepository;


    public List<Customer> getCustomersByIdIn(List<String> idList) {

        final var customerList = customerRepository.findCustomersByIdIn(idList);
        if (customerList.isEmpty()) {
            throw new ApiException(ErrorCode.NO_USERS_FOUND);
        }
        return customerList;
    }


    @Override
    @Transactional
    public void save(Customer grantee) {
        customerRepository.save(grantee);
    }


    public Customer getCustomerById(String grantorId) {

        return customerRepository.findCustomerById(grantorId).orElseThrow(
                () -> new ApiException(ErrorCode.USER_NOT_FOUND)
        );
    }


}
