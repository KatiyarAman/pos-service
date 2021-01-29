package com.ris.inventory.pos.service;

import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.ProductCO;
import com.ris.inventory.pos.model.co.UpdateInventoryCO;
import com.ris.inventory.pos.model.dto.LocationDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.model.dto.UserDTO;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;

import java.util.Date;
import java.util.List;

public interface DiscoveryService {

    public void createMockInventory();

    public List<Product> fetchMockInventory();

    public List<Product> fetchMockInventory(List<ProductCO> productCOList);

    public List<Product> fetchProductInventory(List<ProductCO> productCOList, CurrentUser currentUser);

    public void updateProductInventory(List<UpdateInventoryCO> updateInventory, CurrentUser currentUser);

    public List<UserDTO> fetchUsersByRole(ApplicationRole role, CurrentUser currentUser);

    public PaginationDTO<UserDTO> fetchUsersByRole(int offset, int limit, ApplicationRole role, CurrentUser currentUser);

    public UserDTO fetchUserByUserId(String userId, CurrentUser currentUser);

    public PaginationDTO<UserDTO> fetchUserByNameAndRole(int offset, int limit, String[] name, ApplicationRole role, CurrentUser currentUser);

    public PaginationDTO<UserDTO> fetchUsersByDateAndRole(int offset, int limit, ApplicationRole role, Date start, Date end, CurrentUser currentUser);

    public PaginationDTO<UserDTO> fetchUsersByLocationAndRole(int offset, int limit, ApplicationRole role, String locationId, CurrentUser currentUser);

    public List<UserDTO> fetchAllUserByUserIds(List<String> userIds, CurrentUser currentUser);

    public List<LocationDTO> getActiveLocations();
}
