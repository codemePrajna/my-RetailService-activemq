package com.construct.service;

import com.common.entity.Product;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import com.common.util.SharedObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductConstructService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductQueue productQueue;

    public void loadProductDetails() {
        log.info("Initializing database.");
        productRepository.deleteAll();
        productRepository.save(new Product("13860428", 15.0, "USD"));
        productRepository.save(new Product("15117729", 400.0, "USD"));
        productRepository.save(new Product("16483589", 600.0, "USD"));
        productRepository.save(new Product("16696652", 250.0, "USD"));
        productRepository.save(new Product("16752456", 30.0, "USD"));
        log.info("Initializing database complete.");
    }

    /**
     * Clear all the queues whose product details have been served to user
     */
    @Async
    @Scheduled(fixedDelay = 2000)
    public void clearProductQueue() {
        if (productQueue.getProductStateQueue().contains(ProductEnum.SERVED)) {
            List<UUID> productReqIdList = productQueue.getProductStateQueue().entrySet().stream()
                    .filter(entry -> ProductEnum.SERVED.equals(entry.getValue()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            productQueue.getProductQueue().keySet().removeAll(productReqIdList);
            productQueue.getProductStateQueue().keySet().removeAll(productReqIdList);
        }
        if (productQueue.getProductUpdateStateQueue().contains(ProductEnum.SERVED)) {
            List<UUID> productReqIdList = productQueue.getProductUpdateStateQueue().entrySet().stream()
                    .filter(entry -> ProductEnum.SERVED.equals(entry.getValue()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            productQueue.getProductUpdateQueue().keySet().removeAll(productReqIdList);
            productQueue.getProductUpdateStateQueue().keySet().removeAll(productReqIdList);
        }
    }

    public void updateProductDetails() {
        List<UUID> productReqIdList = productQueue.getProductUpdateStateQueue().entrySet().stream()
                .filter(entry -> ProductEnum.PENDING.equals(entry.getValue()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        for (UUID productReqId : productReqIdList) {
            productQueue.getProductUpdateStateQueue().put(productReqId, ProductEnum.STARTED);
            Product product = productQueue.getProductUpdateQueue().get(productReqId);
            Product returnedProduct = productRepository.findByProductId(product.getProductId());
            returnedProduct.setPrice(product.getPrice());
            productRepository.save(returnedProduct);
            productQueue.getProductUpdateStateQueue().put(productReqId, ProductEnum.COMPLETED);
            synchronized (SharedObject.updateObj) {
                SharedObject.updateObj.notify();
            }
        }

    }
}
