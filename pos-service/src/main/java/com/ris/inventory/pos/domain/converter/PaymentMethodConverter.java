package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.PaymentMethod;
import com.ris.inventory.pos.util.enumeration.TransactionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod method) {
        return method.getMethod();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String method) {
        if (method == null)
            return PaymentMethod.ERROR;
        return PaymentMethod.from(method);
    }
}
