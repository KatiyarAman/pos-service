package com.ris.inventory.pos.service.impl;

import com.google.gson.Gson;
import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.ProductCO;
import com.ris.inventory.pos.model.co.ProductDiscoveryCO;
import com.ris.inventory.pos.model.co.UpdateInventoryCO;
import com.ris.inventory.pos.model.dto.*;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.util.enumeration.ApplicationRole;
import com.ris.inventory.pos.util.exception.DiscoveryException;
import com.ris.inventory.pos.util.exception.DiscoveryRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    private static final String X_USER_ID = "X_USER_ID";

    private static final String X_USERNAME = "X_USERNAME";

    private static final String X_ROLE = "X_AUTHORITY";

    private static final String X_LOCATION = "X_LOCATION";

    private static final List<ProductDiscoveryDTO> mockedInventory = new ArrayList<>();

    private final String INVENTORY_SERVICE_NAME = "inventory-service";

    private final String USER_SERVICE_NAME = "user";

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private Logger logger = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    private List<ServiceInstance> getServiceInstance(String serviceName) {
        logger.info("Getting service instance by service name {}", serviceName);
        return discoveryClient.getInstances(serviceName);
    }

    @Override
    public List<Product> fetchProductInventory(List<ProductCO> productCOList, CurrentUser currentUser) {
        logger.info("Going to get the product inventory for {} ", new Gson().toJson(productCOList));

        ServiceInstance serviceInstance = getInventoryService();
        List<Product> products = new ArrayList<>();

        String PATH = "/product/item/location";
        String url = serviceInstance.getUri() + PATH;
        ProductDiscoveryCO productDiscoveryCO = ProductDiscoveryCO.setProductCOList(productCOList);
        HttpHeaders headers = getHeaders(currentUser);

        try {
            logger.info("Requesting inventory-service to get products");
            logger.info("Get Inventory Request URL :{}", url);
            logger.info("Get Inventory Request Headers :{}", new Gson().toJson(headers));
            logger.info("Get Inventory Request Body :{}", new Gson().toJson(productDiscoveryCO));
            HttpEntity<ProductDiscoveryCO> httpEntity = new HttpEntity<>(productDiscoveryCO, headers);

            ResponseEntity<List<ProductDiscoveryDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                    new ParameterizedTypeReference<List<ProductDiscoveryDTO>>() {
                    });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("Product inventory request processed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of product inventory");
                List<ProductDiscoveryDTO> productDiscoveryList = responseEntity.getBody();
                logger.info("Response received from Inventory-Service {}", new Gson().toJson(productDiscoveryList));

                if (productDiscoveryList != null)
                    products = getProductsByDiscoveryDTO(productDiscoveryList);
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            exception.printStackTrace();
            logger.error("Inventory service error during products details fetching process.");
            throw new DiscoveryRequestException("Inventory-Service error during fetching products inventory.");
        }
        return products;
    }

    @Override
    public void updateProductInventory(List<UpdateInventoryCO> updateInventory, CurrentUser currentUser) {
        logger.info("Going to update the product inventory for {} ", new Gson().toJson(updateInventory));

        ServiceInstance serviceInstance = getInventoryService();
        HttpHeaders headers = getHeaders(currentUser);

        try {
            String PATH = "/product/item/location";
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting inventory-service to update product inventory");
            logger.info("Update Inventory Request URL :{}", url);
            logger.info("Update Inventory Request Headers :{}", new Gson().toJson(headers));
            logger.info("Update Inventory Request Body :{}", new Gson().toJson(updateInventory));
            HttpEntity<List<UpdateInventoryCO>> httpEntity = new HttpEntity<>(updateInventory, headers);

            ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Void.class);

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("Update Product inventory request processed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Product inventory updated successfully");
            }

        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("Inventory service error during update products inventory process.");
            throw new DiscoveryRequestException("Inventory-Service error during update products inventory.");
        }
    }

    //TODO fix API url or merge APIs
    @Override
    public List<UserDTO> fetchUsersByRole(ApplicationRole role, CurrentUser currentUser) {
        logger.info("Going to get users for role {} ", role);

        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        List<UserDTO> users = new ArrayList<>();
        try {
            String PATH = "/user/role/" + role.getRole();
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get users by role");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<List<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users == null)
                    return new ArrayList<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get users by role.");
            throw new DiscoveryRequestException("SSO-Service error during fetching users by role.");
        }
        return users;
    }

    //TODO fix API url or merge APIs
    @Override
    public PaginationDTO<UserDTO> fetchUsersByRole(int offset, int limit, ApplicationRole role, CurrentUser currentUser) {
        logger.info("Going to get users for role {} ", role);

        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        PaginationDTO<UserDTO> users = new PaginationDTO<>();
        try {
            String PATH = "/user/list/" + role.getRole() + "?offset=" + offset + "&limit=" + limit;
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get users by role");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<PaginationDTO<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<PaginationDTO<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users == null)
                    return new PaginationDTO<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get users by role.");
            throw new DiscoveryRequestException("SSO-Service error during fetching users by role.");
        }
        return users;
    }

    @Override
    public UserDTO fetchUserByUserId(String userId, CurrentUser currentUser) {
        logger.info("Going to get user for userId {} ", userId);

        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        UserDTO user = null;

        if (userId == null)
            return null;
        try {
            String PATH = "/user/detail/" + userId;
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get user by userId");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<UserDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDTO.class);

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                user = responseEntity.getBody();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get user by userId.");
            throw new DiscoveryRequestException("SSO-Service error during fetching user by userId.");
        }

        return user;
    }

    @Override
    public PaginationDTO<UserDTO> fetchUserByNameAndRole(int offset, int limit, String[] name, ApplicationRole role, CurrentUser currentUser) {

        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        PaginationDTO<UserDTO> users = new PaginationDTO<>();
        try {
            String PATH = "/user/list/salespersons/name?" + "firstName=" + name[0] + "&" + "lastName=" + name[1] + "&" + "authority=" + role.getRole() + "&offset="
                    + offset + "&limit=" + limit;
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get users by name and role");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<PaginationDTO<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<PaginationDTO<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users == null)
                    return new PaginationDTO<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get users by name and role.");
            throw new DiscoveryRequestException("SSO-Service error during fetching users by name and role.");
        }
        return users;
    }

    @Override
    public PaginationDTO<UserDTO> fetchUsersByDateAndRole(int offset, int limit, ApplicationRole role, Date start, Date end, CurrentUser currentUser) {
        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        String startDate = formatter.format(start);
        String endDate = formatter.format(end);

        PaginationDTO<UserDTO> users = new PaginationDTO<>();
        try {
            String PATH = "/user/list/date?" + "start=" + startDate + "&" + "end=" + endDate + "&authority=" + role.getRole() + "&offset=" + offset + "&limit=" + limit;
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get users by date and role");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<PaginationDTO<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<PaginationDTO<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users == null)
                    return new PaginationDTO<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get users by date and role.");
            throw new DiscoveryRequestException("SSO-Service error during fetching users by date and role.");
        }
        return users;
    }

    @Override
    public PaginationDTO<UserDTO> fetchUsersByLocationAndRole(int offset, int limit, ApplicationRole role, String locationId, CurrentUser currentUser) {
        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        PaginationDTO<UserDTO> users = new PaginationDTO<>();
        try {
            String PATH = "/location/user/role?" + "locationId=" + locationId + "&authority=" + role.getRole() + "&offset=" + offset + "&limit=" + limit;
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get users by location and role");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<PaginationDTO<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<PaginationDTO<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users == null)
                    return new PaginationDTO<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get users by location and role.");
            throw new DiscoveryRequestException("SSO-Service error during fetching users by location and role.");
        }
        return users;
    }

    @Override
    public List<UserDTO> fetchAllUserByUserIds(List<String> userIds, CurrentUser currentUser) {
        logger.info("Going to get user for userId {} ", new Gson().toJson(userIds));

        ServiceInstance serviceInstance = getUserService();
        HttpHeaders headers = getHeaders(currentUser);

        List<UserDTO> users = new ArrayList<>();

        if (userIds.isEmpty())
            return users;

        try {
            String PATH = "/user/detail?";
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < userIds.size(); i++) {
                if (i == userIds.size() - 1)
                    builder.append("userId=").append(userIds.get(i));
                else
                    builder.append("userId=").append(userIds.get(i)).append("&");
            }
            String url = serviceInstance.getUri() + PATH + builder.toString();
            logger.info("Requesting sso-service to get user by userId");
            logger.info("Request URL :{}", url);
            logger.info("Request Headers :{}", new Gson().toJson(headers));
            HttpEntity httpEntity = new HttpEntity<>(headers);

            ResponseEntity<List<UserDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<UserDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of users");
                users = responseEntity.getBody();
                if (users != null)
                    users.removeIf(Objects::isNull);
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get user by userId.");
            throw new DiscoveryRequestException("SSO-Service error during fetching user by userId.");
        }

        return users;
    }

    @Override
    public List<LocationDTO> getActiveLocations() {
        logger.info("Going to get locations");

        ServiceInstance serviceInstance = getUserService();

        List<LocationDTO> locations = new ArrayList<>();
        try {
            String PATH = "/location";
            String url = serviceInstance.getUri() + PATH;
            logger.info("Requesting sso-service to get locations");
            logger.info("Request URL :{}", url);
            HttpEntity httpEntity = new HttpEntity<>(serviceInstance);
            ResponseEntity<List<LocationDTO>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<LocationDTO>>() {
            });

            int httpStatus = responseEntity.getStatusCodeValue();
            logger.info("SSO service Request completed service HTTP code {}", httpStatus);

            if (httpStatus == HttpStatus.OK.value()) {
                logger.info("Parsing response of locations");
                locations = responseEntity.getBody();
                if (locations == null)
                    return new ArrayList<>();
            }
        } catch (HttpClientErrorException.BadRequest | HttpClientErrorException.Conflict | HttpClientErrorException.NotFound exception) {
            logger.error("SSO service error during get locations");
            throw new DiscoveryRequestException("SSO-Service error during fetching locations");
        }
        return locations;
    }

    private ServiceInstance getUserService() {
        List<ServiceInstance> serviceInstance = getServiceInstance(USER_SERVICE_NAME);

        if (serviceInstance == null || serviceInstance.isEmpty()) {
            logger.info("SSO Service not found to get the user by role");
            throw new DiscoveryException("Unable to find the running instance of 'SSO Service'.");
        }
        return serviceInstance.get(0);
    }

    private HttpHeaders getHeaders(CurrentUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_ROLE, currentUser.getAuthority().getRole());
        headers.set(X_LOCATION, new Gson().toJson(currentUser.getLocation()));
        headers.set(X_USERNAME, "SYSTEM CALL");
        headers.set(X_USER_ID, currentUser.getUserId());
        return headers;
    }

    private ServiceInstance getInventoryService() {
        List<ServiceInstance> serviceInstance = getServiceInstance(INVENTORY_SERVICE_NAME);
        if (serviceInstance == null || serviceInstance.isEmpty()) {
            logger.info("Inventory Service not found to get the inventory of products.");
            throw new DiscoveryException("Unable to find the running instance of 'Inventory Service'.");
        }
        return serviceInstance.get(0);
    }

    private List<Product> getProductsByDiscoveryDTO(List<ProductDiscoveryDTO> productDiscoveryList) {
        List<Product> products = new ArrayList<>();
        boolean isDevProfile = environment.acceptsProfiles(Profiles.of("dev"));

        for (int i = 0; i < productDiscoveryList.size(); i++) {
            Product product = new Product(productDiscoveryList.get(i), isDevProfile, (long) i + 1);
            products.add(product);
        }

        return products;
    }

    @Override
    public void createMockInventory() {
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            List<ProductDiscoveryDTO> products = new ArrayList<>();
            int UPC = 1000;
            int unitCost = 500;
            int unitSaleCost = 700;
            int id = 159990;

            if (mockedInventory.isEmpty()) {
                for (int i = 1; i <= 10; i++) {
                    ProductDiscoveryDTO product = new ProductDiscoveryDTO();
                    product.setProductId("P-" + (id + i));
                    product.setUpc(String.valueOf(UPC + i));
                    product.setSku(String.valueOf(UPC + i));
                    product.setSize("Mocked Size");
                    product.setCategory("Mocked Men");
                    product.setColor("Mocked Black");
                    product.setUsedBy("Mocked Male Gender");
                    product.setUnitCost(unitCost + (50 * i));
                    product.setUnitSaleCost(unitSaleCost + (75 * i));
                    product.setDiscount(0);
                    product.setAmountForTax(7.5f);
                    product.setIsPercentageTax(true);
                    product.setIsPercentageDiscount(true);
                    product.setSupplier("Mocked Reebok");
                    product.setImage("Mocked Image");
                    product.setDescription("Mocked Description");
                    product.setActualQuantity(10 + i * 2);
                    product.setConsumedQuantity(0);
                    LocationDiscoveryDTO locationDiscoveryDTO = new LocationDiscoveryDTO();
                    locationDiscoveryDTO.setLocationId("L-mocked_" + i);
                    locationDiscoveryDTO.setLocationId("PL-mocked_" + i);
                    locationDiscoveryDTO.setQuantity(10 + i * 2);
                    locationDiscoveryDTO.setConsumedQuantity(0);
                    product.setLocation(locationDiscoveryDTO);
                    products.add(product);
                }
                mockedInventory.addAll(products);
            }
            logger.info("Mocked Inventory is created by '{}' products", mockedInventory.size());
        }
    }

    @Override
    public List<Product> fetchMockInventory() {
        if (environment.acceptsProfiles(Profiles.of("dev")))
            return getProductsByDiscoveryDTO(mockedInventory);
        else
            throw new DiscoveryException("You are not in dev profile to access this resource.");
    }

    @Override
    public List<Product> fetchMockInventory(List<ProductCO> productCOList) {
        List<ProductDiscoveryDTO> filteredProducts = new ArrayList<>();
        for (ProductCO productCO : productCOList) {
            mockedInventory.stream().filter(product -> productCO.getProductId().equals(product.getProductId())).findFirst().ifPresent(filteredProducts::add);
        }
        return getProductsByDiscoveryDTO(filteredProducts);
    }
}
