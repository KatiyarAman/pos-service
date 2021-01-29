package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.TransactionStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {

    @Override
    public String convertToDatabaseColumn(TransactionStatus status) {
        return status.getStatus();
    }

    @Override
    public TransactionStatus convertToEntityAttribute(String status) {
        if (status == null)
            return TransactionStatus.ERROR;
        return TransactionStatus.from(status);
    }
}
