package com.services.service.impl;

import com.common.config.ActiveMQConfig;
import com.common.entity.Product;
import com.common.model.ProductRequest;
import com.common.util.ProductEnum;
import com.common.util.SharedObject;
import com.services.service.ProductResponse;
import com.services.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.xml.ExceptionElementParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductResponse productResponse;

    @Autowired
    private JmsTemplate jmsTemplate;


    @Override
    @JmsListener(destination = ActiveMQConfig.PRODUCT_RESPONSE_QUEUE)
    public void fetchProductDetails(ProductRequest productRequest) throws InterruptedException, RuntimeException {
        Product product = productRequest.getProduct();


        //change the state of the queue such that the entry can be deleted
       /* productQueue.getProductStateQueue().put(productReqId, ProductEnum.SERVED);
        Product product = productRepository.findByProductId(productQueue.getProductQueue().get(productReqId));*/
        if (product == null || product.getName() == null) {
            throw new RuntimeException("Product details not available");
        }
        //acquire lock on the object
        synchronized (SharedObject.sharedObj) {
            try {
                //fetch details of the products which are in pending state
                  if (productRequest.getProductState().equals(ProductEnum.COMPLETED)) {
                //wait until construct module loads the product details onto db
                SharedObject.sharedObj.notify();
                   }
            } catch (Exception e) {
                throw new RuntimeException("Failed to retrieve product details for product");
            }
        }
        productResponse.getProductResponse().put(product.getProductId(),product);
    }

    @Override
    public void insertProductRequest(String productId) {
        ProductRequest productRequest = new ProductRequest();
        //create a unique request id for the request
        UUID productReqId = UUID.randomUUID();
        //inserting the fetch product request to Queue
        productRequest.setProductId(productId);
        productRequest.setProductRequestId(productReqId);
        productRequest.setProductState(ProductEnum.PENDING);
        //push the request to queue
        jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_REQUEST_QUEUE, productRequest);
        /*productQueue.getProductQueue().put(productReqId, productId);
        productQueue.getProductStateQueue().put(productReqId, ProductEnum.PENDING);*/
    }

    @Override
    public void insertProductUpdateRequest(Product product) {
        ProductRequest productRequest = new ProductRequest();
        //create a unique request id for the request
        UUID productReqId = UUID.randomUUID();
        productRequest.setProduct(product);
        productRequest.setProductRequestId(productReqId);
        productRequest.setProductState(ProductEnum.PENDING);
        //push the update price request to queue
        jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_UPDATE_QUEUE, productRequest);
    }
}
