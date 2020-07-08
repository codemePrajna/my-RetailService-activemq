package com.construct.service;

import com.common.util.ProductEnum;
import com.common.util.ProductQueue;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScheduleService {
    @Autowired
    ProductQueue productQueue;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("productNameFetchJob")
    Job job;

    @Autowired
    ProductConstructService productConstructService;

    @Async
    @Scheduled(fixedDelay = 1000)
    public void constructProduct() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        fetchProduct();
        updateProduct();
    }

    private void updateProduct() {
        if (productQueue.getProductUpdateQueue().size() > 0) {
            if (productQueue.getProductUpdateStateQueue().get(productQueue.getProductUpdateStateQueue().keySet().toArray()[0]).equals(ProductEnum.PENDING)) {
                productConstructService.updateProductDetails();

            }
        }
    }

    private void fetchProduct() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        if ((productQueue.getProductQueue().size() > 0)) {
            List<UUID> productReqIdList = productQueue.getProductStateQueue().entrySet().stream()
                    .filter(entry -> ProductEnum.PENDING.equals(entry.getValue()))
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            for (UUID prodReqId : productReqIdList) {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("requestId", prodReqId.toString());
                jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            }
        }
    }
}
