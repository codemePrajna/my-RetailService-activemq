package com.construct.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class ProductFetchProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String s) throws Exception {
        return s;
    }
}
