package com.construct.process;

import com.common.util.SharedObject;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class ProductFetchListener implements JobExecutionListener {
    /*@Autowired
    SharedObject sharedObj;*/
    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        synchronized (SharedObject.sharedObj) {
            SharedObject.sharedObj.notify();
        }
    }
}
