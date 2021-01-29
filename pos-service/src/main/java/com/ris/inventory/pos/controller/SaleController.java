package com.ris.inventory.pos.controller;

import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.*;
import com.ris.inventory.pos.model.dto.InvoiceDTO;
import com.ris.inventory.pos.model.dto.OrderDTO;
import com.ris.inventory.pos.service.DiscoveryService;
import com.ris.inventory.pos.service.SalesService;
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
import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin
@Api(description = "Operations like Place new order, Change existing order, Payment for Order, Refund, Exchange etc", tags = "APIs for Sale")
public class SaleController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private DiscoveryService discoveryService;

    @ApiOperation(value = "Mock Inventory")
    @RequestMapping(value = "/mocked", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public List<Product> fetchMockInventory() {
        return discoveryService.fetchMockInventory();
    }

    @ApiOperation(value = "Place new order")
    @RequestMapping(value = "/place", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OrderDTO placeOrder(@ApiParam(name = " ", value = " ") @RequestBody @Valid OrderCO orderCO, BindingResult bindingResult,
                               @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                               @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return salesService.placeOrder(orderCO, currentUser);
    }

    @ApiOperation(value = "Change in existing order")
    @RequestMapping(value = "/change", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OrderDTO changeOrder(@ApiParam(name = " ", value = " ") @RequestBody @Valid ChangeQuantityCO changeQuantityCO,
                                BindingResult bindingResult, @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                                @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return salesService.updateOrder(changeQuantityCO, currentUser);
    }

    @ApiOperation(value = "Payment for order Transaction (Sale/Refund)")
    @RequestMapping(value = "/payment", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public InvoiceDTO orderPayment(@ApiParam(name = " ", value = " ") @RequestBody @Valid TransactionCO transactionCO, BindingResult bindingResult,
                                   @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                                   @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return salesService.orderPayment(transactionCO, currentUser);
    }

    @ApiOperation(value = "Cancel order")
    @RequestMapping(value = "/{orderId}", method = RequestMethod.DELETE, consumes = "application/json", produces = "application/json")
    public void orderCancel(@PathVariable("orderId") @Valid @NotNull @NotBlank String orderId,
                            @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                            @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        salesService.cancelOrder(orderId, currentUser);
    }

    @ApiOperation(value = "Find order invoices by Mobile, OrderId, InvoiceId")
    @RequestMapping(value = "/invoice/{key}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public List<InvoiceDTO> fetchInvoice(@PathVariable("key") @NotNull @NotBlank String key, @RequestHeader("X_AUTHORITY") String authority,
                                         @RequestHeader("X_USER_ID") String userId, @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                         @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        return salesService.fetchInvoices(key, currentUser);
    }

    @ApiOperation(value = "Fetch partial order details for payment.")
    @RequestMapping(value = "/partial/{orderId}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public OrderDTO fetchPartialOrder(@PathVariable("orderId") @NotNull @NotBlank String orderId, @RequestHeader("X_AUTHORITY") String authority,
                                      @RequestHeader("X_USER_ID") String userId, @RequestHeader("X_USERNAME") @NotNull @NotBlank String username,
                                      @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        return salesService.partialOrder(orderId, currentUser);
    }

    @ApiOperation(value = "Refund existing order products/items")
    @RequestMapping(value = "/refund", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OrderDTO refund(@ApiParam(name = " ", value = " ") @RequestBody @Valid RefundCO refundCO, BindingResult bindingResult,
                           @RequestHeader("X_AUTHORITY") @NotNull @NotBlank String authority, @RequestHeader("X_USER_ID") @NotNull @NotBlank String userId,
                           @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return salesService.refund(refundCO, currentUser);
    }

    @ApiOperation(value = "Exchange existing order products/items")
    @RequestMapping(value = "/exchange", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public OrderDTO exchange(@ApiParam(name = " ", value = " ") @RequestBody @Valid ExchangeCO exchangeCO, BindingResult bindingResult,
                             @RequestHeader("X_AUTHORITY") String authority, @RequestHeader("X_USER_ID") String userId,
                             @RequestHeader("X_USERNAME") @NotNull @NotBlank String username, @RequestHeader("X_LOCATION") String location) {

        CurrentUser currentUser = CurrentUser.getInstance(userId, username, authority, location);

        if (bindingResult.hasErrors())
            throw new BadRequestException("Bad Request. Params Missing");

        return salesService.exchange(exchangeCO, currentUser);
    }
}
