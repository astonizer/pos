package com.increff.invoice.dto;

import com.increff.invoice.model.OrderDetailsForm;
import com.increff.invoice.util.PdfUtil;
import com.increff.invoice.util.StringUtil;
import com.increff.invoice.util.XmlUtil;
import org.apache.fop.apps.FOPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

@Component
@PropertySource("classpath:invoice.properties")
public class InvoiceDto {

    @Autowired
    private Environment environment;

    public String generate(OrderDetailsForm orderDetailsForm) throws ParserConfigurationException, TransformerException, FOPException, IOException {
        String xmlFilePath = environment.getProperty("xml.path"),
                xslFilePath = environment.getProperty("xsl.path");
        XmlUtil.createXml(orderDetailsForm, xmlFilePath);
        File pdfFile = PdfUtil.generatePdfFromTemplate(xmlFilePath, xslFilePath);
        String base64String = StringUtil.encode(pdfFile);
        return base64String;
    }

}
