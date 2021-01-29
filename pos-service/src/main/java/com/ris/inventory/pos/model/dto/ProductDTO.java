package com.ris.inventory.pos.model.dto;

import com.ris.inventory.pos.domain.Product;
import com.ris.inventory.pos.model.response.TransactionResponse;
import com.ris.inventory.pos.util.enumeration.ProductStatus;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Product (In Response)", description = "Response for : Inner object of Invoice")
public class ProductDTO implements TransactionResponse {

    private String productId;

    private String upc; //universal product code

    private String sku; //item short identifier

    private String size;

    private String category; // one to one

    private String color;

    private String usedBy;//usedByGenderId

    private float unitCost;// manufacture cost

    private float unitSaleCost; //sale cost

    private float discount;

    private float taxAmount;

    private boolean isPercentageTax;

    private int refundQuantity;

    private int exchangeQuantity;

    private String supplier;

    private String description;

    private int orderQuantity;

    private ProductStatus productStatus;

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.upc = product.getUpc();
        this.sku = product.getSku();
        this.size = product.getSize();
        this.category = product.getCategory();
        this.color = product.getColor();
        this.usedBy = product.getUsedBy();
        this.unitCost = product.getUnitCost();
        this.unitSaleCost = product.getUnitSaleCost();
        this.discount = product.getDiscount();
        this.taxAmount = product.getAmountForTax();
        this.refundQuantity = product.getRefundQuantity();
        this.exchangeQuantity = product.getExchangeQuantity();
        this.supplier = product.getSupplier();
        this.description = product.getDescription();
        this.orderQuantity = product.getOrderQuantity();
        this.productStatus = product.getProductStatus();
        this.isPercentageTax = product.isPercentageTax();
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isPercentageTax() {
        return isPercentageTax;
    }

    public void setPercentageTax(boolean percentageTax) {
        isPercentageTax = percentageTax;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
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

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(float taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getRefundQuantity() {
        return refundQuantity;
    }

    public void setRefundQuantity(int refundQuantity) {
        this.refundQuantity = refundQuantity;
    }

    public int getExchangeQuantity() {
        return exchangeQuantity;
    }

    public void setExchangeQuantity(int exchangeQuantity) {
        this.exchangeQuantity = exchangeQuantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }
}
