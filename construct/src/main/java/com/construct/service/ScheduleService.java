package com.construct.service;

import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScheduleService {
    @Autowired
    ProductQueue productQueue;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("productNameFetchJob")
    Job job;

    @Autowired
    Executor asyncExecutor;

    @Autowired
    ProductConstructService productConstructService;

    @Async
    @Scheduled(fixedDelay = 1000)
    public void constructProduct() throws Exception {
        fetchProduct();
        updateProduct();
    }

    @Transactional
    public void updateProduct() {
        if (productQueue.getProductUpdateQueue().size() > 0) {
            if (productQueue.getProductUpdateStateQueue().get(productQueue.getProductUpdateStateQueue().keySet().toArray()[0]).equals(ProductEnum.PENDING)) {
                productConstructService.updateProductDetails();

            }
        }
    }

    @Transactional
    public void fetchProduct() throws Exception {
        if ((productQueue.getProductQueue().size() > 0)) {
            List<UUID> productReqIdList = productQueue.getProductStateQueue().entrySet().stream()
                    .filter(entry -> ProductEnum.PENDING.equals(entry.getValue()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            for (UUID prodReqId : productReqIdList) {
                //  CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("requestId", prodReqId.toString());
                try {
                    jobLauncher.run(job, jobParametersBuilder.toJobParameters());
                } catch (JobExecutionAlreadyRunningException e) {
                    log.info("Fetching product details for [{}]", productQueue.getProductQueue().get(prodReqId));
                } catch (Exception e) {
                    throw new RuntimeException("Error occurred while fetching product details");
                }

                //   }, asyncExecutor);
            }
            /*for (UUID prodReqId : productReqIdList) {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("requestId", prodReqId.toString());
                jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            }*/
        }
    }
}
