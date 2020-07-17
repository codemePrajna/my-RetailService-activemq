package com.construct.process;

import com.common.config.ActiveMQConfig;
import com.common.entity.Product;
import com.common.model.ProductRequest;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.construct.util.ProductQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Writer class to save the consolidated product details to db
 */
@Component
@Slf4j
@StepScope
public class ProductFetchWriter implements ItemWriter<String> {
    @Value("#{jobParameters['requestId']}")
    protected String requestId;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductQueue productQueue;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void write(List<? extends String> list) throws Exception {
        ProductRequest productRequest = productQueue.getProductQueue().get(UUID.fromString(requestId));
        //String productId = productRequest.getProductIdFromReqId(UUID.fromString(requestId));
        Product product = productRepository.findByProductId(productRequest.getProductId());
        product.setName(list.get(0));
        productRepository.save(product);
        productQueue.getProductQueue().get(UUID.fromString(requestId)).setProductState(ProductEnum.COMPLETED);
        productRequest.setProduct(product);
        //insert the response of the product details to product response queue
        jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_RESPONSE_QUEUE, productRequest);

    }
}
