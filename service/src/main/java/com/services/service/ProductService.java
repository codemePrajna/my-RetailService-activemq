package com.services.service;

import com.common.entity.Product;

import java.util.UUID;

public interface ProductService {
    public Product fetchProductDetails(UUID productReqId) throws InterruptedException, RuntimeException;

    public String updateProduct(String productId);

    public UUID insertProductRequest(String productId);

    public UUID insertProductUpdateRequest(String productId);
}
