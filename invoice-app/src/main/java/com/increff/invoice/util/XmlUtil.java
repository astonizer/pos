package com.increff.invoice.util;

import com.increff.invoice.model.OrderDetailsForm;
import com.increff.invoice.model.OrderItemForm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class XmlUtil {

    public static void createXml(OrderDetailsForm orderDetailsForm, String xmlFilePath) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element orderDetailsElement = document.createElement("invoice");
        document.appendChild(orderDetailsElement);

        Element idElement = document.createElement("orderId");
        idElement.appendChild(document.createTextNode(String.valueOf(orderDetailsForm.getId())));
        orderDetailsElement.appendChild(idElement);

        Element nameElement = document.createElement("customer");
        nameElement.appendChild(document.createTextNode(orderDetailsForm.getName()));
        orderDetailsElement.appendChild(nameElement);

        Element dateElement = document.createElement("invoiceDate");
        dateElement.appendChild(document.createTextNode(String.valueOf(orderDetailsForm.getInvoicedDateTime().toLocalDate())));
        orderDetailsElement.appendChild(dateElement);

        Element timeElement = document.createElement("invoiceTime");
        timeElement.appendChild(document.createTextNode(
                orderDetailsForm.getInvoicedDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        orderDetailsElement.appendChild(timeElement);

        Element orderItemsElement = document.createElement("orderItems");
        orderDetailsElement.appendChild(orderItemsElement);

        for (OrderItemForm orderItem : orderDetailsForm.getOrderItemForms()) {
            Element orderItemElement = document.createElement("orderItem");
            orderItemsElement.appendChild(orderItemElement);

            Element itemDescriptionElement = document.createElement("description");
            itemDescriptionElement.appendChild(document.createTextNode(orderItem.getName()));
            orderItemElement.appendChild(itemDescriptionElement);

            Element itemQuantityElement = document.createElement("quantity");
            itemQuantityElement.appendChild(document.createTextNode(String.valueOf(orderItem.getQuantity())));
            orderItemElement.appendChild(itemQuantityElement);

            Element itemPriceElement = document.createElement("price");
            itemPriceElement.appendChild(document.createTextNode((new DecimalFormat("#.##").format(orderItem.getSellingPrice())) + " Rs."));
            orderItemElement.appendChild(itemPriceElement);

            Double itemTotal = orderItem.getSellingPrice() * (double)orderItem.getQuantity();

            Element itemAmountElement = document.createElement("amount");
            itemAmountElement.appendChild(document.createTextNode(((new DecimalFormat("#.##").format(itemTotal)) + " Rs.")));
            orderItemElement.appendChild(itemAmountElement);

        }

        Element total = document.createElement("total");
        total.appendChild(document.createTextNode((new DecimalFormat("#.##").format(orderDetailsForm.getTotal())) + " Rs."));
        orderDetailsElement.appendChild(total);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);

        StreamResult streamResult = new StreamResult(new File(xmlFilePath));
        transformer.transform(domSource, streamResult);
    }

    private static String getDate() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date dateobj = new Date();
        return df.format(dateobj);
    }

    private static Double round(Double fieldValue) {
        return Math.round(fieldValue * 100.0) / 100.0;
    }

}