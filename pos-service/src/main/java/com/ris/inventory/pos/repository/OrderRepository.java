package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.*;
import com.ris.inventory.pos.model.Pagination;
import com.ris.inventory.pos.util.enumeration.OrderStatus;
import org.hibernate.Interceptor;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends GenericRepository<Order> {

    public Order initiateOrder(List<Product> products, Customer customer, Interceptor interceptor);

    public Order initiateOrder(List<Product> products, Customer customer, Delivery delivery, Interceptor interceptor);

    public boolean updateDelivery(Order order, Delivery delivery, Interceptor interceptor);

    public void changeQuantity(List<Product> products, Interceptor interceptor);

    public void cancelOrder(Order order, Interceptor interceptor);

    public void updateOrder(Order order, OrderStatus status, Interceptor interceptor);

    public boolean updateProducts(List<Product> products, Interceptor interceptor);

    public Order findByCustomer(Customer customer);

    public List<Order> findAllByCustomer(Customer customer);

    public Integer getTotalRefundedQuantity(Order order, String productId);

    public Integer getTotalExchangedQuantity(Order order, String productId);

    public Order findByTransaction(Transaction transaction);

    public List<Product> getProductByOrder(Order order);

    public Order get(Long id);

    public Order get(String orderId);

    public List<Order> list(Pagination pagination);

    public List<Order> list(Pagination pagination, String userIdORLocation);

    public List<Order> findAllByDate(Pagination pagination, Date start, Date end);

    public List<Order> findAllByDateAndLocation(Pagination pagination, Date start, Date end, String locationId);

    public List<Order> findAllByDateAndUserId(Pagination pagination, Date start, Date end, String userId);
}
