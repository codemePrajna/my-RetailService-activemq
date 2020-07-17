package com.services.service;

import com.common.entity.Product;
import com.common.model.ProductRequest;

import java.util.UUID;

public interface ProductService {
    public void fetchProductDetails(ProductRequest productRequest) throws InterruptedException, RuntimeException;

    // public Product updateProductDetails(UUID productReqId) throws InterruptedException;

    public UUID insertProductRequest(String productId);

    public UUID insertProductUpdateRequest(Product product);
}
