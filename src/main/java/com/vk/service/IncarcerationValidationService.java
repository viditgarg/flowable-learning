package com.vk.service;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("incarcerationValidationService")
@RequiredArgsConstructor
public class IncarcerationValidationService implements JavaDelegate {

    private final Logger log = LoggerFactory.getLogger(IncarcerationValidationService.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("validating incarceration status for ::" + execution.getVariable("firstName") + " " + execution.getVariable("lastName"));

        boolean incarcerated = true;
        // external services can be called
        //call doccs and rikers services
        execution.setVariable("incarcerationStatus", incarcerated);
        execution.setVariable("incarcerationStatus", incarcerated);
    }
}
