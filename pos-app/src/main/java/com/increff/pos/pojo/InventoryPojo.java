package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "inventory")
public class InventoryPojo extends AbstractPojo {

    // Product ID
    @Id
    private Integer productId;

    @Column(nullable = false)
    private Integer quantity;

}
