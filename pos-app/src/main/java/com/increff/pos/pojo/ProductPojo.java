package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(
    name = "products",
    uniqueConstraints = {
            @UniqueConstraint(name = "uk_barcode", columnNames = "barcode")
    },
        indexes = @Index(name = "idx_brandCategory", columnList = "brandCategory")
)
public class ProductPojo extends AbstractPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Integer brandCategory;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double mrp;

}
