package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Delivery;
import com.ris.inventory.pos.domain.Order;
import org.hibernate.Interceptor;

import java.util.Map;

public interface DeliveryRepository {

    public String createDelivery(Map<String, Object> delivery, Interceptor interceptor);

    public String updateDelivery(Order order, Delivery delivery);

    public Delivery get(Order order);

    public Delivery get(String deliveryId);

    public Delivery get(Long id);
}
