package com.vk.service;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("createPersonService")
@RequiredArgsConstructor
public class CreatePersonService implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(CreatePersonService.class);
    @Override
    public void execute(DelegateExecution execution) {
        log.info("Creating New Person ::"+execution.getVariable("firstName")+" "+execution.getVariable("lastName"));
        // execution.va
        log.info("person created");
    }
}
