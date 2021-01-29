package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Min;

public class PaymentCO {

    @NonNull
    private PaymentMethod method;

    @Min(1)
    private float amount;

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
