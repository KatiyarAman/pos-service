package com.ris.inventory.pos.model.co;

import java.util.ArrayList;
import java.util.List;

public class ProductDiscoveryCO {

    private List<String> products = new ArrayList<>();

    public static ProductDiscoveryCO setProductCOList(List<ProductCO> products) {
        ProductDiscoveryCO productDiscoveryCO = new ProductDiscoveryCO();

        products.forEach(
                it -> {
                    productDiscoveryCO.setProducts(it.getProductId());
                }
        );

        return productDiscoveryCO;

    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public void setProducts(String product) {
        if (product != null)
            this.products.add(product);
    }
}
