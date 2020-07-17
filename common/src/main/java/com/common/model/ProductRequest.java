package com.common.model;

import com.common.entity.Product;
import com.common.util.ProductEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Data
@Component
public class ProductRequest {
    String productId;
    UUID productRequestId;
    String productUpdateId;
    Product product;
    ProductEnum productState;
}
