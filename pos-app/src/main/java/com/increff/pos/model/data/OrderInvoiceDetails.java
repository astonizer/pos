package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderInvoiceDetails {

    private String name;
    private Integer id;
    private ZonedDateTime invoicedDateTime;
    private OrderInvoiceItemDetails[] orderItemForms;
    private Double total;

}
