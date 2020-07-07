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

import java.util.UUID;

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
            if (productQueue.getProductStateQueue().get(productQueue.getProductQueue().keySet().toArray()[0]).equals(ProductEnum.PENDING)) {
                UUID requestId = (UUID) productQueue.getProductStateQueue().keySet().toArray()[0];
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("requestId", requestId.toString());
                jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            }
        }
    }
}
