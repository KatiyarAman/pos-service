package com.ris.inventory.pos.model.co;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "Product Quantity", description = "Request for : Inner object of 'Change in existing order request'")
public class QuantityCO {

    @NotNull
    @NotBlank
    private String productId;

    @Min(0)
    private int quantity;

    private boolean isNew;

    @JsonProperty("isNew")
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty("isNew")
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
