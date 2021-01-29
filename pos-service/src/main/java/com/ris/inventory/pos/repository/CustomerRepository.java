package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Customer;
import com.ris.inventory.pos.repository.criteria.CustomerCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerCriteriaRepository {

}
