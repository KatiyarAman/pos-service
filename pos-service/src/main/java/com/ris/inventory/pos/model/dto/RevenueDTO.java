package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.model.response.TransactionResponse;

public class RevenueDTO implements TransactionResponse {

    private String date;

    private float amount;

    public RevenueDTO() {
    }

    public RevenueDTO(String date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
