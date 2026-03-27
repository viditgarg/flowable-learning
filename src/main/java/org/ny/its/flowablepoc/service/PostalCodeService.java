package org.ny.its.flowablepoc.service;

import jakarta.xml.soap.*;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class PostalCodeService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String city = (String) execution.getVariable("city");
        String state = (String) execution.getVariable("state");

        if (city == null || state == null) {
            execution.setVariable("postalCode", "");
            return;
        }

        try {
                SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
                String url = "http://www.crcind.com/csp/samples/SOAP.Demo.cls";
                // Call SOAP webservice to get postal code
                SOAPMessage request = createSOAPRequest(city, state);
                SOAPMessage response = soapConnection.call(request, url);
                String postalCode = response.getSOAPBody().getTextContent();
                execution.setVariable("postalCode", postalCode);
            } catch (Exception e) {
                e.printStackTrace();
                execution.setVariable("postalCode", "");
            }

        //execution.setVariable("postalCode", postalCode);
    }
    private SOAPMessage createSOAPRequest(String city, String state) throws Exception {

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody body = envelope.getBody();

        // Namespace (important for real SOAP services)
        envelope.addNamespaceDeclaration("ns", "http://tempuri.org/");

        SOAPElement lookup = body.addChildElement("ZipCodeLookup", "ns");

        lookup.addChildElement("City", "ns").addTextNode(city);
        lookup.addChildElement("State", "ns").addTextNode(state);

        soapMessage.saveChanges();
        return soapMessage;
    }
    private String callPostalCodeSoapService(String city, String state) {
        // Example: Use Spring WebServiceTemplate or JAX-WS client
        // For now, return a dummy value
        return "12345";
    }

    private String getPostalCodeFromSoapResponse(SOAPMessage response) throws SOAPException {
        SOAPBody body = response.getSOAPBody();
        SOAPElement responseElement = (SOAPElement) body.getFirstChild();

        return responseElement
                .getElementsByTagName("Zip")
                .item(0)
                .getTextContent();
    }
}