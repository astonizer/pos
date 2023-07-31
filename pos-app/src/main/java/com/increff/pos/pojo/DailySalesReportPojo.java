package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "salesReport")
public class DailySalesReportPojo extends AbstractPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer invoicedOrdersCount;

    @Column(nullable = false)
    private Integer invoicedItemsCount;

    @Column(nullable = false)
    private Double totalRevenue;

}
