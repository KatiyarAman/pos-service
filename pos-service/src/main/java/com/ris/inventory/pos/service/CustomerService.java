package com.ris.inventory.pos.service;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.CustomerCO;
import com.ris.inventory.pos.model.co.CustomerUpdateCO;
import com.ris.inventory.pos.model.dto.CustomerDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;

public interface CustomerService {

    public CustomerDTO save(CustomerCO customerCO, CurrentUser currentUser);

    public CustomerDTO update(CustomerUpdateCO customerCO, String customerId, CurrentUser currentUser);

    public CustomerDTO findByMobile(String mobile);

    public CustomerDTO get(String customerId);

    public CustomerDTO get(Long id);

    public PaginationDTO<CustomerDTO> list(int offset, int limit);

    public boolean isExists(String mobile);
}
