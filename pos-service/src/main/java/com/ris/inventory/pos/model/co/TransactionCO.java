package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionType;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Payment", description = "Request for : Payment of order")
public class TransactionCO {

    @NotNull
    @NotBlank
    private String orderId;

    private List<PaymentCO> payments = new ArrayList<>();

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private TransactionType paidFor;

    public List<PaymentCO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentCO> payments) {
        this.payments = payments;
    }

    public TransactionType getPaidFor() {
        return paidFor;
    }

    public void setPaidFor(TransactionType paidFor) {
        this.paidFor = paidFor;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
