package com.ris.inventory.pos.domain;


import com.ris.inventory.pos.domain.converter.ProductStatusConverter;
import com.ris.inventory.pos.model.dto.ProductDiscoveryDTO;
import com.ris.inventory.pos.util.enumeration.ProductStatus;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product")
public class Product implements Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

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

    @Column(updatable = false, columnDefinition = "double default 0.0")
    private float amountForTax;

    private boolean isPercentageTax = true;

    private boolean isPercentageDiscount = true;

    private String supplier;

    private String image;

    private String description;

    private int actualQuantity;

    private int consumeQuantity;

    private String productLocationId;

    private int orderQuantity;

    private int refundQuantity;

    private int exchangeQuantity;

    private String location;

    @Convert(converter = ProductStatusConverter.class)
    private ProductStatus productStatus;

    @ManyToOne
    @JoinColumn(nullable = false, name = "orderId")
    private Order order;

    @Column(name = "isActive")
    private boolean isActive;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true)
    @org.hibernate.annotations.UpdateTimestamp
    private Date lastModified;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date dateDeleted;

    public Product() {
        this.isDeleted = false;
        this.isActive = true;
    }

    public Product(Product product) {
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
        this.amountForTax = product.getAmountForTax();
        this.isPercentageTax = product.isPercentageTax();
        this.supplier = product.getSupplier();
        this.image = product.getImage();
        this.description = product.getDescription();
        this.actualQuantity = product.getActualQuantity();
        this.orderQuantity = product.getOrderQuantity();
        this.location = product.getLocation();
        this.isPercentageDiscount = product.isPercentageDiscount();
        this.consumeQuantity = product.getConsumeQuantity();
        this.productLocationId = product.getProductLocationId();
        this.isDeleted = false;
        this.isActive = true;
    }

    public Product(ProductDiscoveryDTO productDiscovery, boolean isDevProfile, Long id) {
        if (isDevProfile)
            this.id = id;

        this.productId = productDiscovery.getProductId();
        this.upc = productDiscovery.getUpc();
        this.sku = productDiscovery.getSku();
        this.size = productDiscovery.getSize();
        this.category = productDiscovery.getCategory();
        this.color = productDiscovery.getColor();
        this.usedBy = productDiscovery.getUsedBy();
        this.unitCost = productDiscovery.getUnitCost();
        this.unitSaleCost = productDiscovery.getUnitSaleCost();
        this.discount = productDiscovery.getDiscount();
        this.amountForTax = productDiscovery.getAmountForTax();
        this.isPercentageTax = productDiscovery.getIsPercentageTax();
        this.isPercentageDiscount = productDiscovery.getIsPercentageDiscount();
        this.supplier = productDiscovery.getSupplier();
        this.image = productDiscovery.getImage();
        this.description = productDiscovery.getDescription();
        this.actualQuantity = productDiscovery.getLocation().getQuantity();
        this.consumeQuantity = productDiscovery.getLocation().getConsumedQuantity();
        this.location = productDiscovery.getLocation().getLocationId();
        this.productLocationId = productDiscovery.getLocation().getProductLocationId();
        this.isDeleted = false;
        this.isActive = true;
    }

    public boolean isPercentageDiscount() {
        return isPercentageDiscount;
    }

    public void setPercentageDiscount(boolean percentageDiscount) {
        isPercentageDiscount = percentageDiscount;
    }

    public int getConsumeQuantity() {
        return consumeQuantity;
    }

    public void setConsumeQuantity(int consumeQuantity) {
        this.consumeQuantity = consumeQuantity;
    }

    public String getProductLocationId() {
        return productLocationId;
    }

    public void setProductLocationId(String productLocationId) {
        this.productLocationId = productLocationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public float getAmountForTax() {
        return amountForTax;
    }

    public void setAmountForTax(float amountForTax) {
        this.amountForTax = amountForTax;
    }

    public boolean isPercentageTax() {
        return isPercentageTax;
    }

    public void setPercentageTax(boolean percentageTax) {
        isPercentageTax = percentageTax;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
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

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "{" + "productId='" + productId + '\'' + ", orderQuantity=" + orderQuantity + ", productStatus=" + productStatus + "}";
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }
}
