package com.services.service;

import com.common.entity.Product;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Data
public class ProductResponse {
    ConcurrentHashMap<UUID, Product> productResponse = new ConcurrentHashMap<>();
}
