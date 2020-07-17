package com.construct.config;

import com.construct.util.ProductQueue;
import com.construct.process.ProductFetchListener;
import com.construct.process.ProductFetchProcessor;
import com.construct.process.ProductFetchReader;
import com.construct.process.ProductFetchWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableBatchProcessing
public class ProductConstructConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    BeanFactory beanFactory;
    @Autowired
    ProductQueue productQueue;

    /**
     * Name fetch batch job
     *
     * @return
     */
    @Bean(name = "productNameFetchJob")
    public Job productFetchJob() {
        return jobBuilderFactory.get("productNameFetchStep")
                .incrementer(new RunIdIncrementer()).listener(productNameFetchListener())
                .flow(productNameFetchStep()).end().build();
    }

    @Bean
    public Step productNameFetchStep() {
        ProductFetchReader productFetchReader = beanFactory.getBean(ProductFetchReader.class);
        ProductFetchProcessor productFetchProcessor = beanFactory.getBean(ProductFetchProcessor.class);
        ProductFetchWriter productFetchWriter = beanFactory.getBean(ProductFetchWriter.class);

        return stepBuilderFactory.get("productNameFetchJobListener").<String, String>chunk(1).reader(productFetchReader)
                .processor(productFetchProcessor).writer(productFetchWriter).build();
    }

    @Bean
    public JobExecutionListener productNameFetchListener() {
        return new ProductFetchListener();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
