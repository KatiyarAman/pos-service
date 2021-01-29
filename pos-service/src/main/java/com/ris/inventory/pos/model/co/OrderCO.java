package com.ris.inventory.pos.model.co;

import io.swagger.annotations.ApiModel;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Order (In Request)", description = "Request for : Place new order")
public class OrderCO {

    @NotNull
    @NotBlank
    private String customerId;

    private String deliveryId;

    @NotNull
    @Valid
    private List<ProductCO> products;

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<ProductCO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCO> products) {
        this.products = products;
    }
}
