package com.common.util;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class ProductQueue {
    public ConcurrentHashMap<UUID, String> productQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, ProductEnum> productStateQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, String> productUpdateQueue = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, ProductEnum> productUpdateStateQueue = new ConcurrentHashMap<>();
}
