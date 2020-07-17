package com.construct.service;

import com.common.config.ActiveMQConfig;
import com.common.entity.Product;
import com.common.model.ProductRequest;
import com.common.repository.ProductRepository;
import com.common.util.ProductEnum;
import com.common.util.TrackTimeUtil;
import com.construct.util.ProductQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductConstructService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductQueue productQueue;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("productNameFetchJob")
    Job job;

    @Autowired
    private JmsTemplate jmsTemplate;

    @TrackTimeUtil
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


    private void updateProductDetails(ProductRequest productRequest) {

        productRequest.setProductState(ProductEnum.STARTED);
        Product product = productRequest.getProduct();
        Product returnedProduct = productRepository.findByProductId(product.getProductId());
        returnedProduct.setPrice(product.getPrice());
        productRepository.save(returnedProduct);
        productRequest.setProductState(ProductEnum.COMPLETED);
        productRequest.setProduct(returnedProduct);
        //notify service that the product details have been updated
        //insert the response of the product details to product response queue
        jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_RESPONSE_QUEUE, productRequest);

    }

    /**
     * Function to update the product information received from user
     *
     * @param productRequest
     */
    @TrackTimeUtil
    @JmsListener(destination = ActiveMQConfig.PRODUCT_UPDATE_QUEUE)
    public void updateProduct(ProductRequest productRequest) {
        if (checkIfProductExists(productRequest)) {
            updateProductDetails(productRequest);
        }


    }

    /**
     * fetch the product information for the request
     * consolidate with title information from target url
     *
     * @param productRequest
     * @throws Exception
     */
    @TrackTimeUtil
    @JmsListener(destination = ActiveMQConfig.PRODUCT_REQUEST_QUEUE)
    public void fetchProduct(ProductRequest productRequest) throws Exception {
        if (productRequest.getProductRequestId() != null) {
            if (checkIfProductExists(productRequest)) {
                //  CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("requestId", productRequest.getProductRequestId().toString());
                //receive the product request and place it in the map for reader to pick
                productQueue.getProductQueue().put(productRequest.getProductRequestId(), productRequest);
                try {
                    jobLauncher.run(job, jobParametersBuilder.toJobParameters());
                } catch (JobExecutionAlreadyRunningException e) {
                    log.info("Fetching product details for [{}]", productQueue.getProductQueue().get(productRequest.getProductRequestId()));
                } catch (Exception e) {
                    throw new RuntimeException("Error occurred while fetching product details");
                }

                //   }, asyncExecutor);
                //   }

            }
        }


    }

    private boolean checkIfProductExists(ProductRequest productRequest) {
        Product product = productRepository.findByProductId(productRequest.getProductId());
        if (product == null) {
            productRequest.setProductState(ProductEnum.FAILED);
            jmsTemplate.convertAndSend(ActiveMQConfig.PRODUCT_RESPONSE_QUEUE, productRequest);
            return false;
        }
        return true;
    }
}
