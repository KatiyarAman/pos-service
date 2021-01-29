package com.ris.inventory.pos.model.co;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Exchange", description = "Request for : Exchange order/products of order")
public class ExchangeCO {

    @NotBlank
    @NotNull
    private String orderId;

    private String deliveryId;

    @NotNull
    private List<ProductCO> products;

    @NotNull
    private List<ProductCO> replacements;

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<ProductCO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCO> products) {
        this.products = products;
    }

    public List<ProductCO> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<ProductCO> replacements) {
        this.replacements = replacements;
    }
}
