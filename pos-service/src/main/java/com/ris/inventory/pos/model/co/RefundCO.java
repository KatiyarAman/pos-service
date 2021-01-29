package com.ris.inventory.pos.model.co;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Refund", description = "Request for : Refund order/products of order")
public class RefundCO {

    @NotBlank
    @NotNull
    private String orderId;

    @NotNull
    private Boolean isComplete;

    @NotNull
    private List<ProductCO> products;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public List<ProductCO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCO> products) {
        this.products = products;
    }
}
