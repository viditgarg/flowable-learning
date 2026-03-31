package org.ny.its.flowablepoc.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class PostalCodeSearchService {

    // private static final String SOAP_URL =
    // "http://www.crcind.com/csp/samples/SOAP.Demo.cls";
    // private static final String SOAP_ACTION =
    // "http://tempuri.org/SOAP.Demo.LookupCity";
    private static final String REST_URL = "https://api.zippopotam.us/us/";

    public Address lookupCityByZip(String zip) throws Exception {
        URL url = new URL(REST_URL + zip);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        String json = response.toString();
        // simple parsing (no library for now)
        String city = json.split("\"place name\": \"")[1].split("\"")[0];
        String state = json.split("\"state abbreviation\": \"")[1].split("\"")[0];

        return new Address("N/A", city, state, zip);

    }

    // Simple Address DTO
    public static class Address {
        public String street;
        public String city;
        public String state;
        public String zip;

        public Address(String street, String city, String state, String zip) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zip = zip;
        }

        @Override
        public String toString() {
            return street + ", " + city + ", " + state + " " + zip;
        }
    }

    /*
     * public Address lookupCityByZip(String zipCode) throws Exception {
     * // Build SOAP XML request
     * String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
     * + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" "
     * + "xmlns:tem=\"http://tempuri.org\">"
     * + "<soap:Body>"
     * + "<tem:LookupCity>"
     * + "<tem:zip>" + zipCode + "</tem:zip>"
     * + "</tem:LookupCity>"
     * + "</soap:Body>"
     * + "</soap:Envelope>";
     * 
     * // Open HTTP connection
     * URL url = new URL(SOAP_URL);
     * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     * conn.setRequestMethod("POST");
     * conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
     * conn.setRequestProperty("SOAPAction", SOAP_ACTION);
     * conn.setDoOutput(true);
     * 
     * // Send SOAP request
     * try (OutputStream os = conn.getOutputStream()) {
     * os.write(soapXml.getBytes("UTF-8"));
     * }
     * 
     * // Read response safely
     * InputStream is;
     * int status = conn.getResponseCode();
     * if (status >= 400) {
     * is = conn.getErrorStream(); // Handle HTTP error
     * } else {
     * is = conn.getInputStream();
     * }
     * 
     * StringBuilder sb = new StringBuilder();
     * try (BufferedReader br = new BufferedReader(new InputStreamReader(is,
     * "UTF-8"))) {
     * String line;
     * while ((line = br.readLine()) != null) {
     * sb.append(line.trim()); // trim whitespace
     * }
     * }
     * 
     * String responseStr = sb.toString();
     * // Remove BOM if present
     * responseStr = responseStr.replace("\uFEFF", "").trim();
     * 
     * // Quick check for valid SOAP
     * if (!responseStr.contains("<SOAP-ENV:Envelope")) {
     * throw new RuntimeException("Invalid SOAP response: " + responseStr);
     * }
     * 
     * // Parse SOAP XML
     * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
     * factory.setNamespaceAware(true);
     * DocumentBuilder builder = factory.newDocumentBuilder();
     * Document doc = builder.parse(new InputSource(new StringReader(responseStr)));
     * 
     * // Extract LookupCityResult
     * NodeList resultList = doc.getElementsByTagNameNS("http://tempuri.org",
     * "LookupCityResult");
     * if (resultList.getLength() == 0) {
     * throw new RuntimeException("No LookupCityResult found in SOAP response");
     * }
     * 
     * Element result = (Element) resultList.item(0);
     * String city = getTextContent(result, "City");
     * String state = getTextContent(result, "State");
     * String zip = getTextContent(result, "Zip");
     * String street = "100 private way"; // SOAP service doesn't return street
     * 
     * return new Address(street, city, state, zip);
     * }
     */

    /*
     * // Helper to safely get element text
     * private String getTextContent(Element parent, String tagName) {
     * NodeList list = parent.getElementsByTagName(tagName);
     * return (list.getLength() > 0) ? list.item(0).getTextContent() : "";
     * }
     */
}
