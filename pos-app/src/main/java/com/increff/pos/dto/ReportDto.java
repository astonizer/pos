package com.increff.pos.dto;

import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.*;
import com.increff.pos.service.*;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.ConversionUtil;
import com.increff.pos.util.NormalizeUtil;
import com.increff.pos.util.ValidateUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ReportDto {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ReportService reportService;

    public List<DailySalesReportData> getDailySalesReportList() throws ApiException {
        List<DailySalesReportPojo> dailySalesReportPojoList = reportService.getDailySalesReport();

        return ConversionUtil.convert(
                dailySalesReportPojoList,
                DailySalesReportData.class
        );
    }

    public List<BrandReportData> getBrandReportList() throws ApiException {
        List<BrandPojo> brandPojoList = brandService.getAll();

        return ConversionUtil.convert(
                brandPojoList,
                BrandReportData.class
        );
    }

    public List<SalesReportData> getSalesReportList(SalesReportForm salesReportForm) throws ApiException {
        NormalizeUtil.normalize(salesReportForm);
        ValidateUtil.validateForm(salesReportForm);

        if(salesReportForm.getStartDate().compareTo(salesReportForm.getEndDate()) > 0) {
            throw new ApiException("Start date should be less than end date");
        }

        List<OrderItemPojo> orderItemPojoList = findInvoicedOrderItemsInRange(salesReportForm.getStartDate(), salesReportForm.getEndDate());

        Map<Integer, List<Integer>> productIdToOrderItemIndexMap = IntStream
                .range(0, orderItemPojoList.size())
                .boxed()
                .collect(Collectors.groupingBy(
                    idx -> orderItemPojoList.get(idx).getProductId(),
                    Collectors.mapping(idx -> idx, Collectors.toList())
                ));

        List<BrandPojo> brandPojoList = brandService.getAll(salesReportForm.getBrand(), salesReportForm.getCategory());

        Map<Integer, BrandPojo> brandIdToBrandPojoMap = brandPojoList
                .stream()
                .collect(Collectors.toMap(BrandPojo::getId, brandPojo -> brandPojo));

        List<Integer> brandIdList = brandPojoList
                .stream()
                .map(BrandPojo::getId)
                .collect(Collectors.toList());

        List<ProductPojo> productPojoList = productService.getAllByBrandIdList(brandIdList)
                .stream()
                .filter(productPojo -> productIdToOrderItemIndexMap.containsKey(productPojo.getId()))
                .collect(Collectors.toList());

        Map<Integer, List<Integer>> brandIdToProductIdListMap = getBrandIdToProductIdListMap(productPojoList);
        Map<Integer, Pair<Integer, Double>> productIdToPairOfQuantityAndRevenueMap = getProductIdToPairOfQuantityAndRevenueMap(orderItemPojoList);

        return generateSalesReportData(productPojoList, brandIdToBrandPojoMap, brandIdToProductIdListMap, productIdToPairOfQuantityAndRevenueMap);
    }

    public List<InventoryReportData> getInventoryReportList() throws ApiException {
        // Find total inventory for every brand/category
        Map<Integer, Integer> inventoryIdtoQuantityMap = getInventoryIdToQuantityMap();

        List<ProductPojo> productPojoList = getProductList();
        Map<Integer, Integer> brandIdToInventoryMap = productPojoList
                .stream()
                .collect(Collectors.groupingBy(
                        ProductPojo::getBrandCategory,
                        Collectors.summingInt(
                                productPojo -> inventoryIdtoQuantityMap.getOrDefault(productPojo.getId(), 0)
                        )
                ));

        // Convert map to data list
        List<InventoryReportData> inventoryReportDataList = new ArrayList<>();

        List<BrandPojo> brandPojoList = brandService.getAll();

        for(BrandPojo brandPojo: brandPojoList) {
            InventoryReportData inventoryReportData = ConversionUtil.convert(
                    brandPojo,
                    InventoryReportData.class
            );

            inventoryReportData.setQuantity(brandIdToInventoryMap.getOrDefault(brandPojo.getId(), 0));

            inventoryReportDataList.add(inventoryReportData);
        }
        return inventoryReportDataList;
    }

    private SalesReportData createSalesReportData(String brand, String category, Integer quantity, Double revenue) {
        SalesReportData salesReportData = new SalesReportData();

        salesReportData.setBrand(brand);
        salesReportData.setCategory(category);
        salesReportData.setQuantity(quantity);
        salesReportData.setRevenue(revenue);

        return salesReportData;
    }

    private Map<Integer, Integer> getInventoryIdToQuantityMap() {
        List<InventoryPojo> inventoryPojoList = inventoryService.getAll();

        return inventoryPojoList
                .stream()
                .collect(Collectors.toMap(
                        InventoryPojo::getProductId,
                        InventoryPojo::getQuantity
                ));
    }

    private List<ProductPojo> getProductList() {
        return productService.getAll();
    }

    private List<OrderItemPojo> findInvoicedOrderItemsInRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        List<OrderPojo> orderPojoList = orderService.findInvoicedOrdersInRange(startDate, endDate);

        return orderPojoList
                .stream()
                .flatMap(orderPojo -> orderItemService.getOrderItemsByOrderId(orderPojo.getId()).stream())
                .collect(Collectors.toList());
    }

    private Map<Integer, Pair<Integer, Double>> getProductIdToPairOfQuantityAndRevenueMap(List<OrderItemPojo> invoicedOrderItemsList) {
        Map<Integer, Pair<Integer, Double>> productIdToPairOfQuantityAndRevenueMap = new HashMap<>();

        for(OrderItemPojo orderItemPojo: invoicedOrderItemsList) {
            Double revenue = (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());

            productIdToPairOfQuantityAndRevenueMap.compute(orderItemPojo.getProductId(), (key, oldValue) -> {
                Integer newQuantity = (oldValue != null) ? oldValue.getKey() + orderItemPojo.getQuantity() : orderItemPojo.getQuantity();
                Double newRevenue = (oldValue != null) ? oldValue.getValue() + revenue : revenue;

                return new Pair<>(newQuantity, newRevenue);
            });
        }

        return productIdToPairOfQuantityAndRevenueMap;
    }

    private Map<Integer, List<Integer>> getBrandIdToProductIdListMap(List<ProductPojo> productPojoList) {
        return productPojoList.stream()
                .collect(Collectors.groupingBy(ProductPojo::getBrandCategory,
                        Collectors.mapping(ProductPojo::getId, Collectors.toList())));
    }

    private List<SalesReportData> generateSalesReportData(
            List<ProductPojo> productPojoList,
            Map<Integer, BrandPojo> brandIdToBrandPojoMap,
            Map<Integer, List<Integer>> brandIdToProductIdListMap,
            Map<Integer, Pair<Integer, Double>> productIdToPairOfQuantityAndRevenueMap
    ) {
        return productPojoList.stream()
            .map(productPojo -> brandIdToBrandPojoMap.get(productPojo.getBrandCategory()))
            .map(brandPojo -> {
                List<Integer> productIds = brandIdToProductIdListMap.getOrDefault(brandPojo.getId(), new ArrayList<>());

                Integer quantity = productIds.stream()
                        .mapToInt(productId -> productIdToPairOfQuantityAndRevenueMap.getOrDefault(productId, new Pair<>(0, 0d)).getKey())
                        .sum();

                Double revenue = productIds.stream()
                        .map(productId -> productIdToPairOfQuantityAndRevenueMap.getOrDefault(productId, new Pair<>(0, 0d)).getValue())
                        .reduce(0d, Double::sum);

                return createSalesReportData(brandPojo.getBrand(), brandPojo.getCategory(), quantity, revenue);
            })
            .collect(Collectors.toList());
    }

}
