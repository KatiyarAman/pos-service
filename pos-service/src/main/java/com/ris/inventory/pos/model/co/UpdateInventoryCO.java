package com.ris.inventory.pos.model.co;

import com.ris.inventory.pos.util.enumeration.TransactionType;

public class UpdateInventoryCO {

    private String location;

    private int quantity;

    private TransactionType transactionType;

    public UpdateInventoryCO(int quantity, TransactionType type, String location) {
        this.location = location;
        this.transactionType = type;
        if (type.equals(TransactionType.SALE))
            this.quantity = quantity;
        if (type.equals(TransactionType.REFUND))
            this.quantity = quantity;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
