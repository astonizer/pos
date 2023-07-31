package com.increff.pos.service;

import com.increff.pos.model.data.OrderInvoiceDetails;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.spring.ApplicationProperties;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
@Service
@Log4j
public class ClientWrapper {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    public String generateInvoice(OrderInvoiceDetails orderInvoiceDetails) throws ApiException {
        try {
            return restTemplate
                    .postForObject(applicationProperties.INVOICE_APP_URL, orderInvoiceDetails, String.class);
        } catch (RestClientException e) {
            log.error("Error calling the invoice API: " + e.getMessage());

            throw new ApiException("There was an internal server error, please try again later");
        }
    }

}
