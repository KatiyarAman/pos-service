package com.ris.inventory.pos.model.dto;

public class LocationDiscoveryDTO {

    private String locationId;

    private String productLocationId;

    private int quantity;

    private int consumedQuantity;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getProductLocationId() {
        return productLocationId;
    }

    public void setProductLocationId(String productLocationId) {
        this.productLocationId = productLocationId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }
}
