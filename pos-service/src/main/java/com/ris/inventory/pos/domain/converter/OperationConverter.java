package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.Operation;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class OperationConverter implements AttributeConverter<Operation, String> {

    @Override
    public String convertToDatabaseColumn(Operation type) {
        return type.getType();
    }

    @Override
    public Operation convertToEntityAttribute(String type) {
        if (type == null)
            return Operation.ERROR;
        return Operation.from(type);
    }
}
