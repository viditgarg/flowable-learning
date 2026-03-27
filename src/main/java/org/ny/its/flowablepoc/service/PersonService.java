package org.ny.its.flowablepoc.service;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

    }
}
