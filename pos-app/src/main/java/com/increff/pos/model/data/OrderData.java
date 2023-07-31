package com.increff.pos.model.data;

import com.increff.pos.model.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderData {

    private Integer id;
    private String name;
    private OrderStatus status;
    private ZonedDateTime createdAt;
    private List<OrderItemData> orderItemDataList;

}
