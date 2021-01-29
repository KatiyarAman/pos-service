package com.ris.inventory.pos.repository.criteria.impl;

import com.ris.inventory.pos.domain.Delivery;
import com.ris.inventory.pos.domain.Order;
import com.ris.inventory.pos.repository.DeliveryRepository;
import com.ris.inventory.pos.util.enumeration.CarrierType;
import com.ris.inventory.pos.util.enumeration.DeliveryStatus;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class DeliveryRepositoryImpl extends GenericRepositoryImpl<Delivery> implements DeliveryRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(DeliveryRepositoryImpl.class);

    @Override
    public String createDelivery(Map<String, Object> delivery, Interceptor interceptor) {
        logger.info("creating delivery for {}", delivery);
        Delivery deliveryEntity = new Delivery(Float.parseFloat(String.valueOf(delivery.get("amount"))), (CarrierType) delivery.get("carrier"));
        super.save(deliveryEntity, interceptor);
        return deliveryEntity.getDeliveryId();
    }

    @Override
    public String updateDelivery(Order order, Delivery delivery) {
        String deliveryId = null;
        Transaction transaction = null;
        try (Session session = getCurrentSession()) {
            transaction = session.beginTransaction();
            Delivery mergedDelivery = (Delivery) session.merge(delivery);
            mergedDelivery.setOrder(order);
            mergedDelivery.setStatus(DeliveryStatus.CREATED);
            session.saveOrUpdate(mergedDelivery);
            transaction.commit();
            deliveryId = delivery.getDeliveryId();
        } catch (Exception exp) {
            exp.printStackTrace();
            logger.error("Exception while updating delivery for order");
            rollBackTransaction(transaction);
        }
        return deliveryId;
    }

    @Override
    public Delivery get(Order order) {
        return super.findByColumn("order", order);
    }

    @Override
    public Delivery get(String deliveryId) {
        return super.findByColumn("deliveryId", deliveryId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Delivery.class);
    }
}
