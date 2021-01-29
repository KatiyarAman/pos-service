package com.ris.inventory.pos.model.dto;

public class ProductDiscoveryDTO {

    private String productId;

    private int actualQuantity;

    private String color;

    private int consumedQuantity;

    private String description;

    private float discount;

    private String image;

    private String size;

    private String sku; //item short identifier

    private String upc; //universal product code

    private float unitCost;// manufacture cost

    private float unitSaleCost; //sale cost

    private String category; // one to one

    private String usedBy;//usedByGenderId

    private float amountForTax;

    private Boolean isPercentageTax;

    private Boolean isPercentageDiscount;

    private String supplier;

    private LocationDiscoveryDTO location;

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public Boolean getIsPercentageTax() {
        return isPercentageTax;
    }

    public void setIsPercentageTax(Boolean percentageTax) {
        isPercentageTax = percentageTax;
    }

    public Boolean getIsPercentageDiscount() {
        return isPercentageDiscount;
    }

    public void setIsPercentageDiscount(Boolean percentageDiscount) {
        isPercentageDiscount = percentageDiscount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }

    public float getUnitSaleCost() {
        return unitSaleCost;
    }

    public void setUnitSaleCost(float unitSaleCost) {
        this.unitSaleCost = unitSaleCost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public float getAmountForTax() {
        return amountForTax;
    }

    public void setAmountForTax(float amountForTax) {
        this.amountForTax = amountForTax;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocationDiscoveryDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDiscoveryDTO location) {
        this.location = location;
    }
}
