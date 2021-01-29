package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.model.response.SalespersonResponse;

public class SalesPersonRevenueDTO implements SalespersonResponse {

    private String name;

    private float sale;

    public SalesPersonRevenueDTO(String name, float sale) {
        this.name = name;
        this.sale = sale;
    }

    public SalesPersonRevenueDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSale() {
        return sale;
    }

    public void setSale(float sale) {
        this.sale = sale;
    }
}
