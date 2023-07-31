package com.increff.pos.service;
import com.increff.pos.model.data.OrderInvoiceDetails;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.spring.ApplicationProperties;
import com.increff.pos.util.AbstractMockitoUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientWrapperTest extends AbstractMockitoUnitTest {

    @InjectMocks
    private ClientWrapper clientWrapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationProperties applicationProperties;

    @Before
    public void setup() {
        initMocks(this);

        applicationProperties = new ApplicationProperties();
        ReflectionTestUtils.setField(applicationProperties, "INVOICE_APP_URL", "http://your-test-invoice-app-url");

        ReflectionTestUtils.setField(clientWrapper, "applicationProperties", applicationProperties);
    }

    // Tests calling rest template successfully
    @Test
    public void testGet1() throws ApiException {
        String mockResponse = "Mock Base64 String";

        when(restTemplate.postForObject(anyString(), any(OrderInvoiceDetails.class), eq(String.class)))
                .thenReturn(mockResponse);

        OrderInvoiceDetails orderInvoiceDetails = new OrderInvoiceDetails();
        String result = clientWrapper.generateInvoice(orderInvoiceDetails);

        verify(restTemplate).postForObject(anyString(), eq(orderInvoiceDetails), eq(String.class));
        assertEquals(mockResponse, result);
    }

    // Tests when invoice generation fails
    @Test
    public void testGet2() throws ApiException {
        when(restTemplate.postForObject(anyString(), any(OrderInvoiceDetails.class), eq(String.class)))
                .thenThrow(new RestClientException("Simulated RestClientException"));

        try {
            clientWrapper.generateInvoice(new OrderInvoiceDetails());
        } catch (ApiException e) {
            assertEquals("There was an internal server error, please try again later", e.getMessage());
        }
    }
}
