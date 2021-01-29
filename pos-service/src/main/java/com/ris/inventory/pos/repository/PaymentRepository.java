package com.ris.inventory.pos.repository;

import com.ris.inventory.pos.domain.Payment;
import org.hibernate.Interceptor;

public interface PaymentRepository {

    public Payment save(Payment payment, Interceptor interceptor);
}
