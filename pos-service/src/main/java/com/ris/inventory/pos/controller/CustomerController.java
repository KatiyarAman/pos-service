package com.ris.inventory.pos.controller;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.CustomerCO;
import com.ris.inventory.pos.model.co.CustomerUpdateCO;
import com.ris.inventory.pos.model.dto.CustomerDTO;
import com.ris.inventory.pos.model.dto.PaginationDTO;
import com.ris.inventory.pos.service.CustomerService;
import com.ris.inventory.pos.util.exception.BadRequestException;
import com.ris.inventory.pos.util.exception.CustomerNotFoundException;
import com.ris.inventory.pos.util.exception.DuplicateRecordException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/customer")
@CrossOrigin
@Api(description = "Operations like Add new Customer, Find existing customer by mobile etc", tags = "APIs for Customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation(value = "Add new customer")
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO save(@ApiParam(name = " ", value = " ") @RequestBody @Valid CustomerCO customerCO, BindingResult bindingResult,
                            @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                            @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        if (customerService.isExists(customerCO.getMobile()))
            throw new DuplicateRecordException("Customer already exists by mobile number " + customerCO.getMobile());

        return customerService.save(customerCO, currentUser);
    }

    @ApiOperation(value = "Update customer")
    @RequestMapping(value = "/{customerId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public CustomerDTO update(@ApiParam(name = " ", value = " ") @RequestBody @Valid CustomerUpdateCO customerUpdateCO,
                              @PathVariable("customerId") @NotNull @NotBlank String customerId, BindingResult bindingResult,
                              @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                              @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return customerService.update(customerUpdateCO, customerId, currentUser);
    }

    @ApiOperation(value = "Find customer by mobile number")
    @RequestMapping(value = "/{mobile}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public CustomerDTO fetchByMobile(@PathVariable("mobile") @Valid @NotNull @NotBlank String mobile,
                                     @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                                     @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        CustomerDTO customer = customerService.findByMobile(mobile);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found by " + mobile);

        return customer;
    }

    @ApiOperation(value = "Customer list")
    @RequestMapping(value = "", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public PaginationDTO<CustomerDTO> list(@RequestParam(name = "offset") int offset, @RequestParam(name = "limit") int limit,
                                           @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        return customerService.list(offset, limit);
    }
}
