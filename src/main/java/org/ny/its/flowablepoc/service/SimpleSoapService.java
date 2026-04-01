package org.ny.its.flowablepoc.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class SimpleSoapService {
    private static final String URL_STR = "https://www.dataaccess.com/webservicesserver/NumberConversion.wso";

    private static final String SOAP_ACTION = "http://www.dataaccess.com/webservicesserver/NumberToWords";

    public String convert(int number) {

        try {
            String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "<soap:Body>"
                    + "<NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">"
                    + "<ubiNum>" + number + "</ubiNum>"
                    + "</NumberToWords>"
                    + "</soap:Body>"
                    + "</soap:Envelope>";

            HttpURLConnection conn = (HttpURLConnection) new URL(URL_STR).openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction", SOAP_ACTION);
            conn.setConnectTimeout(25000);
            conn.setReadTimeout(25000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(soapXml.getBytes());
            }

            InputStream is = conn.getResponseCode() >= 400
                    ? conn.getErrorStream()
                    : conn.getInputStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            String res = response.toString();
            // Regex to extract <NumberToWordsResult> content
            Pattern pattern = Pattern.compile("<(?:\\w+:)?NumberToWordsResult>(.*?)</(?:\\w+:)?NumberToWordsResult>");
            Matcher matcher = pattern.matcher(res);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
//            // Simple parsing (no DOM needed!)
//            if (res.contains("<NumberToWordsResult>")) {
//                return res.split("<NumberToWordsResult>")[1]
//                        .split("</NumberToWordsResult>")[0]
//                        .trim();
//            }

            return "Conversion failed";

        } catch (Exception e) {
            return "SOAP Error";
        }
    }

}
