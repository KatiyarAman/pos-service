package com.ris.inventory.pos.domain.converter;

import com.ris.inventory.pos.util.enumeration.ProductStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {

    @Override
    public String convertToDatabaseColumn(ProductStatus status) {
        return status.getStatus();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String status) {
        if (status == null)
            return ProductStatus.ERROR;
        return ProductStatus.from(status);
    }
}
