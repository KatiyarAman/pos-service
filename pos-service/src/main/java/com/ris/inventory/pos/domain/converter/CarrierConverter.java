package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.CarrierType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CarrierConverter implements AttributeConverter<CarrierType, String> {

    @Override
    public String convertToDatabaseColumn(CarrierType type) {
        return type.getCarrier();
    }

    @Override
    public CarrierType convertToEntityAttribute(String type) {
        if (type == null)
            return CarrierType.ERROR;
        return CarrierType.from(type);
    }
}
