package com.increff.invoice.controller;

import com.increff.invoice.dto.InvoiceDto;
import com.increff.invoice.model.OrderDetailsForm;
import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

@Api
@RestController
public class InvoiceApiController {

	@Autowired
	private InvoiceDto invoiceDto;

	@ApiOperation(value = "Generates string encoded pdf")
	@RequestMapping(path = "/api/generate", method = RequestMethod.POST)
	public String generateStringEncodedPdf(@RequestBody OrderDetailsForm orderDetailsForm) throws ParserConfigurationException, TransformerException, FOPException, IOException {
		return invoiceDto.generate(orderDetailsForm);
	}
}
