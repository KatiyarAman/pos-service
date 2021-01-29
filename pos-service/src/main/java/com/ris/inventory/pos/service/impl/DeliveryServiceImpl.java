package com.ris.inventory.pos.service.impl;

import com.ris.inventory.pos.domain.AuditInterceptor;
import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.DeliveryCO;
import com.ris.inventory.pos.repository.DeliveryRepository;
import com.ris.inventory.pos.service.DeliveryService;
import com.ris.inventory.pos.util.exception.EntityNotPersistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private Logger logger = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Override
    public Map<String, String> setupDelivery(DeliveryCO deliveryCO, CurrentUser currentUser) {
        AuditInterceptor auditInterceptor = new AuditInterceptor(currentUser.getUserId(), currentUser.getAuthority().toString(),
                (String) currentUser.getLocation().get("locationId"));

        String deliveryId = deliveryRepository.createDelivery(deliveryCO.getDeliveryMap(), auditInterceptor);

        if (deliveryId == null) {
            logger.error("delivery is 'null' when entity is in process for persisting.");
            throw new EntityNotPersistException("Error Occurred, During delivery persisting");
        }

        Map<String, String> deliveryMap = new HashMap<>();
        deliveryMap.put("deliveryId", deliveryId);
        return deliveryMap;
    }
}
