package com.increff.pos.controller.api;

import com.increff.pos.model.constants.PaginationConstants;
import com.increff.pos.model.data.OrderItemData;
import com.increff.pos.model.data.PaginationData;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.dto.OrderDto;
import com.increff.pos.model.data.OrderData;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderUpdateForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Gets an order by id")
    @GetMapping("/{id}")
    public OrderData get(@PathVariable Integer id) throws ApiException {
        return orderDto.getCheck(id);
    }

    @ApiOperation(value = "Gets list of all orders")
    @GetMapping("")
    public PaginationData<OrderData> getAll(
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_DRAW) Integer draw,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_START) Integer start,
            @RequestParam(defaultValue = PaginationConstants.DEFAULT_LENGTH) Integer length
    ) throws ApiException {
        return orderDto.getPaginatedList(draw, start, length);
    }

    @ApiOperation(value = "Adds an order")
    @PostMapping("")
    public OrderData add(@RequestBody OrderForm orderForm) throws ApiException {
        return orderDto.add(orderForm);
    }

    @ApiOperation(value = "Adds an order item")
    @PostMapping("/{orderId}")
    public OrderItemData add(@PathVariable Integer orderId, @RequestBody OrderItemForm orderItemForm) throws ApiException {
        return orderDto.addOrderItem(orderId, orderItemForm);
    }

    @ApiOperation(value = "Updates an order")
    @PutMapping("/{id}")
    public OrderData update(@PathVariable Integer id, @RequestBody OrderUpdateForm orderUpdateForm) throws ApiException {
        return orderDto.update(id, orderUpdateForm);
    }

    @ApiOperation(value = "Places an order")
    @PostMapping("/{id}/place")
    public ResponseEntity<byte[]> placeOrder(@PathVariable Integer id) throws ApiException {
        return orderDto.invoiceOrder(id);
    }

    @ApiOperation(value = "Downloads invoice")
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Integer id) throws ApiException {
        return orderDto.downloadInvoice(id);
    }

}
