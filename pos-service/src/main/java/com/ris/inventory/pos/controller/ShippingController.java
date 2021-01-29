package com.ris.inventory.pos.controller;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.DeliveryCO;
import com.ris.inventory.pos.service.DeliveryService;
import com.ris.inventory.pos.util.exception.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping(value = "/order")
@CrossOrigin
@Api(description = "Operations like Create new Delivery etc", tags = "APIs for Delivery")
public class ShippingController {

    @Autowired
    private DeliveryService deliveryService;

    @ApiOperation(value = "Create new delivery")
    @RequestMapping(value = "/shipment", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map<String, String> createShipment(@ApiParam(name = " ", value = " ") @RequestBody @Valid DeliveryCO deliveryCO, BindingResult bindingResult,
                                              @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                                              @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                              @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request, Params Missing.");

        return deliveryService.setupDelivery(deliveryCO, currentUser);
    }
}
