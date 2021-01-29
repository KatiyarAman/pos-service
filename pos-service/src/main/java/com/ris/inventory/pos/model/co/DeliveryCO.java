package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.CarrierType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "Delivery", description = "Request for : New delivery of order")
public class DeliveryCO {

    @Min(1)
    private float amount;

    @NotNull
    private CarrierType carrier;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public CarrierType getCarrier() {
        return carrier;
    }

    public void setCarrier(CarrierType carrier) {
        this.carrier = carrier;
    }


    @ApiModelProperty(hidden = true)
    public Map<String, Object> getDeliveryMap() {
        Map<String, Object> delivery = new HashMap<>();
        delivery.put("amount", this.amount);
        delivery.put("carrier", this.carrier);
        return delivery;
    }
}
