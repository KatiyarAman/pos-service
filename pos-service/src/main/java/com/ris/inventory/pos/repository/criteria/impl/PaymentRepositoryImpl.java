package com.ris.inventory.pos.repository.criteria.impl;

import com.ris.inventory.pos.domain.Payment;
import com.ris.inventory.pos.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl extends GenericRepositoryImpl<Payment> implements PaymentRepository, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(PaymentRepositoryImpl.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setEntity(Payment.class);
    }
}
