package org.ny.its.flowablepoc.service;

import jakarta.xml.soap.*;
import lombok.SneakyThrows;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("postalCodeService")
public class PostalCodeService implements JavaDelegate {

    @Autowired
    private PostalCodeSearchService postalCodeSearchService;

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {

        try {
        String zipCodeInput = (String) execution.getVariable("postalCode"); // from form
        PostalCodeSearchService.Address address = postalCodeSearchService.lookupCityByZip(zipCodeInput);

        execution.setVariable("city", address.city);
        execution.setVariable("state", address.state);


    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to fetch postal code info", e);
    }

        //execution.setVariable("postalCode", postalCode);
    }


}