package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.exception.BadRequestException;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "Product (In Request)", description = "Request for : Inner object of Order, Refund and Exchange")
public class ProductCO {

    @NotNull
    @NotBlank
    private String productId;

    @Min(1)
    private int orderQuantity;

    public ProductCO() {
    }

    public ProductCO(String productId, int orderQuantity) {
        this.productId = productId;
        if (orderQuantity == 0)
            throw new BadRequestException("New Product order quantity can not be 0(zero)");
        this.orderQuantity = orderQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
