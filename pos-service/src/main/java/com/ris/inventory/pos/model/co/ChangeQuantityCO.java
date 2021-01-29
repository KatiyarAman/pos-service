package com.ris.inventory.pos.model.co;

import io.swagger.annotations.ApiModel;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Change Order", description = "Request for : Change in existing order")
public class ChangeQuantityCO {

    @NotBlank
    @NotNull
    private String customerId;

    @NotBlank
    @NotNull
    private String orderId;

    @Valid
    @NotNull
    private List<QuantityCO> productQuantity;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<QuantityCO> getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(List<QuantityCO> productQuantity) {
        this.productQuantity = productQuantity;
    }
}
