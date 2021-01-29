package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.DeliveryStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DeliveryStatusConverter implements AttributeConverter<DeliveryStatus, String> {

    @Override
    public String convertToDatabaseColumn(DeliveryStatus deliveryStatus) {
        return deliveryStatus.getStatus();
    }

    @Override
    public DeliveryStatus convertToEntityAttribute(String status) {
        if (status == null)
            return DeliveryStatus.ERROR;
        return DeliveryStatus.from(status);
    }
}
