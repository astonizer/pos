package com.increff.pos.dto;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.OrderForm;
import com.increff.pos.model.form.OrderItemForm;
import com.increff.pos.model.form.OrderUpdateForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.*;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.flow.OrderFlow;
import com.increff.pos.util.*;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDto {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ClientWrapper clientWrapper;

    public OrderData add(OrderForm orderForm) throws ApiException {
        NormalizeUtil.normalize(orderForm);
        ValidateUtil.validateForm(orderForm);

        List<OrderItemPojo> orderItemPojoList = convertToPojoList(orderForm.getOrderItemFormList());
        List<InventoryPojo> inventoryPojoList = convertToInventoryList(orderForm.getOrderItemFormList());

        inventoryService.checkIfSufficientInventoryExists(inventoryPojoList);

        OrderPojo orderPojo = orderFlow.add(orderForm.getName(), orderItemPojoList, inventoryPojoList);

        return ConversionUtil.convert(
                orderPojo,
                OrderData.class
        );
    }

    public OrderItemData addOrderItem(Integer orderID, OrderItemForm orderItemForm) throws ApiException {
        NormalizeUtil.normalize(orderItemForm);
        ValidateUtil.validateForm(orderItemForm);

        orderService.getCheckCreatedOrder(orderID);

        ProductPojo productPojo = productService.getCheck(orderItemForm.getBarcode());

        OrderItemPojo orderItemPojo = convertToOrderItemPojo(orderItemForm, productPojo.getId());

        orderFlow.addOrderItem(orderID, orderItemPojo);

        return convertToOrderItemData(orderItemPojo, orderItemForm.getBarcode());
    }

    public OrderData getCheck(Integer id) throws ApiException {
        OrderPojo orderPojo = orderService.getCheck(id);

        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsByOrderId(orderPojo.getId());

        return populateOrderData(orderPojo, orderItemPojoList);
    }

    public PaginationData<OrderData> getPaginatedList(Integer draw, Integer start, Integer length) throws ApiException {
        List<OrderPojo> orderPojoList = orderService.getPaginatedList(start, length);

        Integer orderCount = orderService.getCount();

        List<OrderData> orderDataList = convertToOrderDataList(orderPojoList);

        return new PaginationData<>(orderDataList, draw, orderCount, orderCount);
    }

    public OrderData update(Integer orderId, OrderUpdateForm orderUpdateForm) throws ApiException {
        NormalizeUtil.normalize(orderUpdateForm);
        ValidateUtil.validateForm(orderUpdateForm);

        OrderPojo orderPojo = orderService.update(orderId, orderUpdateForm.getName());

        return ConversionUtil.convert(
                orderPojo,
                OrderData.class
        );
    }

    @Transactional(rollbackOn = ApiException.class)
    public ResponseEntity<byte[]> invoiceOrder(Integer orderId) throws ApiException {
        orderService.invoiceOrder(orderId);

        OrderInvoiceDetails orderInvoiceDetails = getOrderInvoiceDetailsData(orderId);

        return orderFlow.invoiceOrder(orderId, orderInvoiceDetails);
    }

    public ResponseEntity<byte[]> downloadInvoice(Integer orderId) throws ApiException {
        OrderInvoiceDetails orderInvoiceDetails = getOrderInvoiceDetailsData(orderId);

        if(FileUtil.doesInvoicePdfExist(orderId)) {
            return FileUtil.downloadInvoice(orderInvoiceDetails);
        } else {
            String base64Str = clientWrapper.generateInvoice(orderInvoiceDetails);

            return FileUtil.saveAndDownloadInvoicePdf(base64Str, orderInvoiceDetails);
        }
    }

    private OrderInvoiceDetails getOrderInvoiceDetailsData(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderService.getCheck(orderId);

        OrderInvoiceDetails orderInvoiceDetails = ConversionUtil.convert(orderPojo, OrderInvoiceDetails.class);
        orderInvoiceDetails.setInvoicedDateTime(orderPojo.getUpdatedAt());

        List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsByOrderId(orderId);

        return getOrderInvoiceItemDetailsList(orderInvoiceDetails, orderItemPojoList);
    }

    private OrderInvoiceItemDetails getOrderInvoiceItemDetails(OrderItemPojo orderItemPojo) throws ApiException {
        OrderInvoiceItemDetails orderInvoiceItemDetails = ConversionUtil.convert(
                orderItemPojo,
                OrderInvoiceItemDetails.class
        );

        ProductPojo productPojo = productService.getCheck(orderItemPojo.getProductId());

        orderInvoiceItemDetails.setName(productPojo.getName());

        return orderInvoiceItemDetails;
    }

    private List<OrderData> convertToOrderDataList(List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderData> orderDataList = new ArrayList<>();

        for(OrderPojo orderPojo: orderPojoList) {
            orderDataList.add(ConversionUtil.convert(
                    orderPojo,
                    OrderData.class
            ));
        }

        return orderDataList;
    }

    private List<OrderItemData> convertToOrderItemDataList(List<OrderItemPojo> orderItemPojoList, Map<Integer, String> productIdToBarcodeMap) throws ApiException {
        List<OrderItemData> orderItemDataList = new ArrayList<>();

        for(OrderItemPojo orderItemPojo: orderItemPojoList) {
            OrderItemData orderItemData = ConversionUtil.convert(
                    orderItemPojo,
                    OrderItemData.class
            );

            orderItemData.setBarcode(productIdToBarcodeMap.get(orderItemPojo.getProductId()));
            orderItemData.setTotal(
                    Double.parseDouble(
                            new DecimalFormat("#.##")
                                    .format(orderItemData.getQuantity() * orderItemData.getSellingPrice())
                    )
            );

            orderItemDataList.add(orderItemData);
        }

        return orderItemDataList;
    }

    private OrderData populateOrderData(OrderPojo orderPojo, List<OrderItemPojo> orderItemPojoList) throws ApiException {
        OrderData orderData = ConversionUtil.convert(
                orderPojo,
                OrderData.class
        );

        List<Integer> productIdList = orderItemPojoList
                .stream()
                .map(OrderItemPojo::getProductId)
                .collect(Collectors.toList());

        Map<Integer, String> productIdToBarcodeMap = getProductIdToBarcodeMap(productIdList);

        orderData.setOrderItemDataList(convertToOrderItemDataList(orderItemPojoList, productIdToBarcodeMap));

        return orderData;
    }

    private List<OrderItemPojo> convertToPojoList(List<OrderItemForm> orderItemFormList) throws ApiException {
        List<String> barcodeList = getBarcodeList(orderItemFormList);

        productService.getCheckByBarcodeList(barcodeList);

        Map<String, Integer> barcodeToProductIdMap = productService.getBarcodeToProductIdMap(barcodeList);

        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();

        for(OrderItemForm orderItemForm: orderItemFormList) {
            OrderItemPojo orderItemPojo = ConversionUtil.convert(
                    orderItemForm,
                    OrderItemPojo.class
            );

            orderItemPojo.setProductId(barcodeToProductIdMap.get(orderItemForm.getBarcode()));

            orderItemPojoList.add(orderItemPojo);
        }
        return orderItemPojoList;
    }

    private List<InventoryPojo> convertToInventoryList(List<OrderItemForm> orderItemFormList) throws ApiException {
        List<String> barcodeList = getBarcodeList(orderItemFormList);

        Map<String, Integer> barcodeToProductIdMap = productService.getBarcodeToProductIdMap(barcodeList);

        List<InventoryPojo> inventoryPojoList = new ArrayList<>();

        for(OrderItemForm orderItemForm: orderItemFormList) {
            InventoryPojo inventoryPojo = new InventoryPojo();

            inventoryPojo.setProductId(barcodeToProductIdMap.get(orderItemForm.getBarcode()));
            inventoryPojo.setQuantity(orderItemForm.getQuantity());

            inventoryPojoList.add(inventoryPojo);
        }

        return inventoryPojoList;
    }

    private Map<Integer, String> getProductIdToBarcodeMap(List<Integer> productIdList) {
        List<ProductPojo> productPojoList = productService.getAllByIdList(productIdList);

        return productPojoList
                .stream()
                .collect(Collectors.toMap(ProductPojo::getId, ProductPojo::getBarcode));
    }

    private OrderInvoiceDetails getOrderInvoiceItemDetailsList(OrderInvoiceDetails orderInvoiceDetails,
                                                               List<OrderItemPojo> orderItemPojoList) throws ApiException {
        double total= 0;

        List<OrderInvoiceItemDetails> orderInvoiceItemDetailsList = new ArrayList<>();

        for(OrderItemPojo orderItemPojo: orderItemPojoList) {
            if(orderItemPojo.getQuantity() > 0) {
                total += orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice();
                orderInvoiceItemDetailsList.add(getOrderInvoiceItemDetails(orderItemPojo));
            }
        }

        if(orderInvoiceItemDetailsList.isEmpty()) {
            throw new ApiException("Cannot invoice order with zero order items!");
        }

        orderInvoiceDetails.setOrderItemForms(orderInvoiceItemDetailsList.toArray(new OrderInvoiceItemDetails[0]));
        orderInvoiceDetails.setTotal(
                Double.parseDouble(
                        new DecimalFormat("#.##")
                                .format(total)
                )
        );

        return orderInvoiceDetails;
    }

    private List<String> getBarcodeList(List<OrderItemForm> orderItemFormList) throws ApiException {
        Set<String> barcodeSet = new HashSet<>();

        ValidateUtil.validateList(orderItemFormList, barcodeSet, (item, set) -> {
            if(!set.add(item.getBarcode())) {
                return new Pair<>(true, "Found item with duplicate barcode");
            }
            return new Pair<>(false, null);
        });

        return new ArrayList<>(barcodeSet);
    }

    private OrderItemPojo convertToOrderItemPojo(OrderItemForm orderItemForm, Integer productId) throws ApiException {
        OrderItemPojo orderItemPojo = ConversionUtil.convert(orderItemForm, OrderItemPojo.class);

        orderItemPojo.setProductId(productId);

        return orderItemPojo;
    }

    private OrderItemData convertToOrderItemData(OrderItemPojo orderItemPojo, String barcode) throws ApiException {
        OrderItemData orderItemData = ConversionUtil.convert(
                orderItemPojo,
                OrderItemData.class
        );

        orderItemData.setBarcode(barcode);
        orderItemData.setTotal(
                Double.parseDouble(
                        new DecimalFormat("#.##")
                        .format(orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice())
                )
        );

        return orderItemData;
    }

}
