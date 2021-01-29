package com.ris.inventory.pos.repository.criteria;

import com.ris.inventory.pos.domain.Address;
import com.ris.inventory.pos.domain.Customer;

import java.util.List;

public interface AddressCriteriaRepository {

    public List<Address> fetchByCustomer(Customer customer);

    public Address get(String addressId);
}
