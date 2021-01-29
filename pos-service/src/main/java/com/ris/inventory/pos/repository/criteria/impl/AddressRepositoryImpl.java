package com.ris.inventory.pos.repository.criteria.impl;

import com.ris.inventory.pos.domain.Address;
import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.repository.criteria.AddressCriteriaRepository;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressRepositoryImpl extends GenericRepositoryImpl<Address> implements AddressCriteriaRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(AddressRepositoryImpl.class);


    @Override
    public List<Address> fetchByCustomer(Customer customer) {
        List<Address> addresses = new ArrayList<>();
        try (Session session = super.getCurrentSession()) {
            Customer mergedCustomer = (Customer) session.merge(customer);
            addresses = mergedCustomer.getAddresses();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception Occurred : While getting customer by order");
        }
        return addresses;
    }

    @Override
    public Address get(String addressId) {
        return super.findByColumn("addressId", addressId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Address.class);
    }
}
