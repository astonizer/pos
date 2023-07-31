package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter @Setter
public class OrderDetailsForm {

    private String name;
    private Integer id;
    private ZonedDateTime invoicedDateTime;
    private OrderItemForm[] orderItemForms;
    private Double total;

}
