package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.CustomerType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CustomerTypeConverter implements AttributeConverter<CustomerType, String> {

    @Override
    public String convertToDatabaseColumn(CustomerType type) {
        return type.getType();
    }

    @Override
    public CustomerType convertToEntityAttribute(String type) {
        if (type == null)
            return CustomerType.ERROR;
        return CustomerType.from(type);
    }
}
