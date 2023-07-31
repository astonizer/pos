package com.increff.pos.controller.api;

import com.increff.pos.dto.OrderItemDto;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.form.OrderItemUpdateForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api/order-items")
public class OrderItemsApiController {

    @Autowired
    private OrderItemDto orderItemDto;

    @ApiOperation(value = "Updates an order item")
    @PutMapping("/{id}")
    public OrderItemData update(@PathVariable Integer id, @RequestBody OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        return orderItemDto.update(id, orderItemUpdateForm);
    }

    @ApiOperation(value = "Deletes an order item")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws ApiException {
        orderItemDto.delete(id);
    }

}
