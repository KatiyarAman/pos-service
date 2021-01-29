package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Address;
import com.ris.inventory.pos.repository.criteria.AddressCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>, AddressCriteriaRepository {

}
