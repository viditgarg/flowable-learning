package org.ny.its.flowablepoc.service;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("personSearchService")
@RequiredArgsConstructor
public class PersonSearchService implements JavaDelegate {
    private final Logger log = LoggerFactory.getLogger(PersonSearchService.class);

    @Override
    public void execute(DelegateExecution execution) {

        // log.info("Searching for ::" + execution.getVariable("firstName") + " " + execution.getVariable("lastName"));
        // execution.va
        boolean exists = false; // Person search service can be called
        execution.setVariable("personExists", exists);

    }
}
