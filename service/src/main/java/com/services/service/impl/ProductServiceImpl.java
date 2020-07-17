package com.services.service.impl;

import com.common.config.ActiveMQConfig;
import com.common.entity.Product;
import com.common.model.ProductRequest;
import com.common.util.ProductEnum;
import com.common.util.SharedObject;
import com.common.util.TrackTimeUtil;
import com.services.service.ProductResponse;
import com.services.service.ProductService;
import lombok.extern.slf4j.Slf4j;
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
    @TrackTimeUtil
    @JmsListener(destination = ActiveMQConfig.PRODUCT_RESPONSE_QUEUE)
    public void fetchProductDetails(ProductRequest productRequest) throws RuntimeException {



        //change the state of the queue such that the entry can be deleted
       /* productQueue.getProductStateQueue().put(productReqId, ProductEnum.SERVED);
        Product product = productRepository.findByProductId(productQueue.getProductQueue().get(productReqId));*/
        if (productRequest == null) {
            log.error("Product details not available");
            throw new RuntimeException("Failed to retrieve product details for product");
        }
        //acquire lock on the object
        synchronized (SharedObject.sharedObj) {

                if(productRequest.getProductState().equals(ProductEnum.FAILED)){
                    //productResponse.getProductResponse().put(productRequest.getProductId(), productRequest.getProduct());
                    SharedObject.sharedObj.notify();

                }
                //fetch details of the products which are in pending state
                if (productRequest.getProductState().equals(ProductEnum.COMPLETED)) {
                    Product product = productRequest.getProduct();
                    //wait until construct module loads the product details onto db
                    productResponse.getProductResponse().put(productRequest.getProductRequestId(), product);
                    SharedObject.sharedObj.notify();
                }

        }

    }

    @Override
    public UUID insertProductRequest(String productId) {
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
        return productReqId;
    }

    @Override
    public UUID insertProductUpdateRequest(Product product) {
        ProductRequest productRequest = new ProductRequest();
        //create a unique request id for the request
        UUID productReqId = UUID.randomUUID();
        productRequest.setProductId(product.getProductId());
        productRequest.setProduct(product);
        productRequest.setProductRequestId(productReqId);
        productRequest.setProductState(ProductEnum.PENDING);
        //push the update price request to queue
        jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_UPDATE_QUEUE, productRequest);
        return productReqId;
    }
}
