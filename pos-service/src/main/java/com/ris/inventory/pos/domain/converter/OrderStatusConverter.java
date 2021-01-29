package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.OrderStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus.getStatus();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String status) {
        if (status == null)
            return OrderStatus.ERROR;
        return OrderStatus.from(status);
    }
}
