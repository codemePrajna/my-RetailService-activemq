package com.construct.process;

import com.common.entity.Product;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public void write(List<? extends String> list) throws Exception {
        String productId = productQueue.getProductQueue().get(UUID.fromString(requestId));
        Product product = productRepository.findByProductId(productId);
        product.setName(list.get(0));
        productRepository.save(product);
        productQueue.getProductStateQueue().put(UUID.fromString(requestId), ProductEnum.COMPLETED);

    }
}
