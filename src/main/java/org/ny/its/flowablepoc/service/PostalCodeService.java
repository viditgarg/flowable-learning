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

    @Autowired
    private SimpleSoapService  simpleSoapService;

    @SneakyThrows
    @Override
    public void execute(DelegateExecution execution) {

        try {
        String zipCodeInput = (String) execution.getVariable("postalCode"); // from form
        PostalCodeSearchService.Address address = postalCodeSearchService.lookupCityByZip(zipCodeInput);

        execution.setVariable("city", address.city);
        execution.setVariable("state", address.state);

        // SOAP call (reuse your service)
        String numberInWords = "INVALID";

        try {
            int zip = Integer.parseInt(zipCodeInput);
            numberInWords = simpleSoapService.convert(zip);
        } catch (NumberFormatException ex) {
            execution.setVariable("soapFailed", true);
        }

        execution.setVariable("postalCodeInWords", numberInWords != null ? numberInWords : "UNKNOWN");
       // execution.setVariable("postalCodeInWords", numberInWords);

    } catch (Exception e) {
        e.printStackTrace();
        execution.setVariable("postalCodeInWords", "UNKNOWN");
        execution.setVariable("soapFailed", true);
        throw new RuntimeException("Failed to fetch postal code info", e);
    }

        //execution.setVariable("postalCode", postalCode);
    }


}