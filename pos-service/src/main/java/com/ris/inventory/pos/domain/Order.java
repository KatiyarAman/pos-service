package com.ris.inventory.pos.domain;

import com.ris.inventory.pos.domain.converter.OrderStatusConverter;
import com.ris.inventory.pos.util.enumeration.OrderStatus;
import org.hibernate.Interceptor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String orderId;

    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinTable(name = "customer_orders", joinColumns = {@JoinColumn(name = "orderId", unique = true)},
            inverseJoinColumns = {@JoinColumn(name = "customerId")})
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<Product> products;

    private transient int productCount;

    private String userId;

    private String location;

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

    public Order() {
        this.orderId = generateOrderId();
        this.orderStatus = OrderStatus.INITIALIZED;
        this.isDeleted = false;
    }

    public Order(Interceptor interceptor) {
        this.orderId = generateOrderId();
        this.orderStatus = OrderStatus.INITIALIZED;
        this.isDeleted = false;
        AuditInterceptor auditInterceptor = ((AuditInterceptor) interceptor);
        this.userId = auditInterceptor.getUserId();
        this.location = auditInterceptor.getLocation();
    }


    private String generateOrderId() {
        return "O-" + new Date().getTime();
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        if (this.products == null)
            this.products = new ArrayList<>();
    }

    public void setProduct(Product product) {
        if (this.products == null)
            this.products = new ArrayList<>();

        if (product != null)
            this.products.add(product);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
