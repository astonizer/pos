package com.increff.pos.pojo;

import com.increff.pos.model.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(
        name = "orders",
        indexes = @Index(name = "idx_status", columnList = "status")
)
public class OrderPojo extends AbstractPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    /**
        "created": Indicates an active state
        "invoiced": Indicates a finished state
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

}
