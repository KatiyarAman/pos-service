package com.ris.inventory.pos.service.impl;

import com.ris.inventory.pos.domain.Address;
import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.model.co.CustomerCO;
import com.ris.inventory.pos.model.co.CustomerUpdateCO;
import com.ris.inventory.pos.model.dto.CustomerDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.repository.AddressRepository;
import com.ris.inventory.pos.repository.CustomerRepository;
import com.ris.inventory.pos.service.CustomerService;
import com.ris.inventory.pos.util.ObjectBinder;
import com.ris.inventory.pos.util.exception.CustomerNotFoundException;
import com.ris.inventory.pos.util.exception.DuplicateRecordException;
import com.ris.inventory.pos.util.exception.EntityNotPersistException;
import com.ris.inventory.pos.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectBinder objectBinder;

    private AuditInterceptor initializeInterceptor(CurrentUser currentUser) {
        return new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));
    }

    @Override
    public CustomerDTO save(CustomerCO customerCO, CurrentUser currentUser) {
        AuditInterceptor interceptor = initializeInterceptor(currentUser);

        Customer customer = customerRepository.save(customerCO, interceptor);
        if (customer == null)
            throw new EntityNotPersistException("Customer is not able to save due to some system error. Please try again");
        return objectBinder.bindCustomer(customer, addressRepository);
    }

    @Override
    public CustomerDTO update(CustomerUpdateCO customerCO, String customerId, CurrentUser currentUser) {
        Customer customer = customerRepository.get(customerId);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found by this customerId " + customerId);

        if (!customer.getMobile().equals(customerCO.getMobile()) && customerRepository.findByMobile(customerCO.getMobile()) != null)
            throw new DuplicateRecordException("Customer already exists with this mobile " + customerCO.getMobile());

        customer.setEmail(customerCO.getEmail());
        customer.setCustomerType(customerCO.getCustomerType());
        customer.setMobile(customerCO.getMobile());
        customer.setFirstName(customerCO.getFirstName());
        customer.setLastName(customerCO.getLastName());

        if (customerCO.getAddressId() == null) {
            List<Address> addressList = addressRepository.fetchByCustomer(customer);
            Address address = new Address(customerCO.getAddress());
            addressList.add(address);
            customer.setAddresses(addressList);
        }
        Address address = addressRepository.get(customerCO.getAddressId());
        if (address == null)
            throw new NotFoundException("Address not found with this addressId " + customerCO.getAddressId());

        address.update(customerCO.getAddress());
        addressRepository.save(address);

        Customer updatedCustomer=customerRepository.save(customer);
        return objectBinder.bindCustomer(updatedCustomer, addressRepository);
    }

    @Override
    public CustomerDTO findByMobile(String mobile) {
        return objectBinder.bindCustomer(customerRepository.findByMobile(mobile), addressRepository);
    }

    @Override
    public CustomerDTO get(String customerId) {
        return objectBinder.bindCustomer(customerRepository.get(customerId), addressRepository);
    }

    @Override
    public CustomerDTO get(Long id) {
        return objectBinder.bindCustomer(customerRepository.get(id), addressRepository);
    }

    @Override
    public PaginationDTO<CustomerDTO> list(int offset, int limit) {
        Pagination pagination = new Pagination(limit, offset);

        List<Customer> customers = customerRepository.list(pagination);
        customerRepository.sort(customers, "id");
        return new PaginationDTO<>(objectBinder.bindCustomer(customers, addressRepository), pagination.getCount());
    }

    @Override
    public boolean isExists(String mobile) {
        return findByMobile(mobile) != null;
    }
}
