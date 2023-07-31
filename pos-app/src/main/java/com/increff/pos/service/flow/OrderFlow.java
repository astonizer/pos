package com.increff.pos.service.flow;

import com.increff.pos.model.data.OrderInvoiceDetails;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.FileUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Log4j
public class OrderFlow {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ClientWrapper clientWrapper;

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo add(String name, List<OrderItemPojo> orderItemPojoList, List<InventoryPojo> inventoryPojoList) throws ApiException {
        OrderPojo orderPojo = orderService.add(name);

        addOrderItems(orderItemPojoList, inventoryPojoList, orderPojo.getId());

        return orderPojo;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo addOrderItem(Integer orderId, OrderItemPojo orderItemPojo) throws ApiException {
        inventoryService.decreaseQuantity(
                orderItemPojo.getProductId(),
                orderItemPojo.getQuantity()
        );

        return orderItemService.add(orderId, orderItemPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ResponseEntity<byte[]> invoiceOrder(Integer orderId, OrderInvoiceDetails orderInvoiceDetails) throws ApiException {
        try {
            String base64Str = clientWrapper.generateInvoice(orderInvoiceDetails);

            ResponseEntity<byte[]> responseEntity =  FileUtil.saveAndDownloadInvoicePdf(base64Str, orderInvoiceDetails);

            orderService.invoiceOrder(orderId);

            return responseEntity;
        } catch (ApiException e) {
            log.error("Failed generating invoice: " + e.getMessage());

            throw new ApiException("There was some internal server error. Please try again later.");
        }
    }

    private List<OrderItemPojo> addOrderItems(List<OrderItemPojo> orderItemPojoList,
                                              List<InventoryPojo> inventoryPojoList, Integer orderId) throws ApiException {
        inventoryService.decreaseQuantityOfList(inventoryPojoList);

        orderItemPojoList.forEach(orderItemPojo -> orderItemPojo.setOrderId(orderId));

        return orderItemService.add(orderItemPojoList);
    }

}
