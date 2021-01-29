package com.ris.inventory.pos.service;

import com.ris.inventory.pos.model.CurrentUser;
import com.ris.inventory.pos.model.co.DeliveryCO;

import java.util.Map;

public interface DeliveryService {

    public Map<String, String> setupDelivery(DeliveryCO deliveryCO, CurrentUser currentUser);
}
