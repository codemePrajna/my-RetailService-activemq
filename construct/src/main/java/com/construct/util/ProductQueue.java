package com.construct.util;

import com.common.entity.Product;
import com.common.model.ProductRequest;
import com.common.util.ProductEnum;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class ProductQueue {
    public ConcurrentHashMap<UUID, ProductRequest> productQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, ProductEnum> productStateQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, Product> productUpdateQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, ProductEnum> productUpdateStateQueue = new ConcurrentHashMap<>();
}
