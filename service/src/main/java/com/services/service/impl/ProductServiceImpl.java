package com.services.service.impl;

import com.common.entity.Product;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import com.common.util.SharedObject;
import com.services.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductQueue productQueue;

    @Override
    public Product fetchProductDetails(UUID productReqId) throws InterruptedException, RuntimeException {
        boolean productRetrieved = false;
        //acquire lock on the object
        synchronized (SharedObject.sharedObj) {
            try {
                //fetch details of the products which are in pending state
                if (!productQueue.getProductStateQueue().get(productReqId).equals(ProductEnum.COMPLETED)) {
                    //wait until construct module loads the product details onto db
                    SharedObject.sharedObj.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to retrieve product details for product");
            }
        }
        //change the state of the queue such that the entry can be deleted
        productQueue.getProductStateQueue().put(productReqId, ProductEnum.SERVED);
        Product product = productRepository.findByProductId(productQueue.getProductQueue().get(productReqId));
        if (product == null || product.getName() == null) {
            throw new RuntimeException("Product details not available");
        }
        return product;
    }

    @Override
    public Product updateProductDetails(UUID productReqId) throws InterruptedException {
        //fetch the details of the product with updated price
        synchronized (SharedObject.updateObj) {
            try {
                //fetch details of the products which are in pending state
                if (!productQueue.getProductUpdateStateQueue().get(productReqId).equals(ProductEnum.COMPLETED)) {
                    //wait until construct module loads the product details onto db
                    SharedObject.updateObj.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to retrieve product details for product");
            }
        }

        String productId = productQueue.getProductUpdateQueue().get(productReqId).getProductId();
        UUID fetchProductReqId = insertProductRequest(productId);
        Product updatedProduct = fetchProductDetails(fetchProductReqId);
        productQueue.getProductStateQueue().put(productReqId, ProductEnum.SERVED);
        if (updatedProduct == null || updatedProduct.getName() == null) {
            throw new RuntimeException("Product details not updated successfully");
        }

        return updatedProduct;
    }

    @Override
    public UUID insertProductRequest(String productId) {
        UUID productReqId = UUID.randomUUID();
        productQueue.getProductQueue().put(productReqId, productId);
        productQueue.getProductStateQueue().put(productReqId, ProductEnum.PENDING);
        return productReqId;
    }

    @Override
    public UUID insertProductUpdateRequest(Product product) {
        UUID productReqId = UUID.randomUUID();
        productQueue.getProductUpdateQueue().put(productReqId, product);
        productQueue.getProductUpdateStateQueue().put(productReqId, ProductEnum.PENDING);
        return productReqId;
    }
}
