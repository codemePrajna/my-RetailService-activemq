package com.services.service;

import com.common.entity.Product;

import java.util.UUID;

public interface ProductService {
    public Product fetchProductDetails(UUID productReqId) throws InterruptedException, RuntimeException;

    public Product updateProductDetails(UUID productReqId) throws InterruptedException;

    public UUID insertProductRequest(String productId);

    public UUID insertProductUpdateRequest(Product product);
}
