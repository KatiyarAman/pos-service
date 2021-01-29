package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.TransactionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {

    @Override
    public String convertToDatabaseColumn(TransactionType type) {
        return type.getType();
    }

    @Override
    public TransactionType convertToEntityAttribute(String type) {
        if (type == null)
            return TransactionType.ERROR;
        return TransactionType.from(type);
    }
}
