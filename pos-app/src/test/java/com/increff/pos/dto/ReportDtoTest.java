package com.increff.pos.dto;

import com.increff.pos.dao.ReportDao;
import com.increff.pos.model.data.*;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.schedulers.DailySalesReportScheduler;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;

public class ReportDtoTest extends AbstractUnitTest {

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private ReportDto reportDto;

    // Tests fetching daily sales report
    @Test
    public void testGet1() throws ApiException {
        Double totalRevenue = 2.0;
        apiTestUtil.addDailySalesReport(1, totalRevenue, 3);

        List<DailySalesReportData> dailySalesReportDataList = reportDto.getDailySalesReportList();
        assertEquals(1, dailySalesReportDataList.size());
        assertEquals((Integer)1, dailySalesReportDataList.get(0).getInvoicedItemsCount());
        assertEquals(totalRevenue, dailySalesReportDataList.get(0).getTotalRevenue());
        assertEquals((Integer)3, dailySalesReportDataList.get(0).getInvoicedOrdersCount());
    }

    // Tests fetching brand report
    @Test
    public void testGet2() throws ApiException {
        List<String> brandList = IntStream.range(0, 3).mapToObj(id -> ("brand" + id)).collect(Collectors.toList());
        List<String> categoryList = IntStream.range(0, 3).mapToObj(id -> ("category" + id)).collect(Collectors.toList());
        IntStream.range(0, 3).forEach(id -> apiTestUtil.addBrand(brandList.get(id), categoryList.get(id)));

        List<BrandReportData> brandReportDataList = reportDto.getBrandReportList();
        IntStream.range(0, 3).forEach(id -> {
           assertEquals(brandList.get(id), brandReportDataList.get(id).getBrand());
           assertEquals(categoryList.get(id), brandReportDataList.get(id).getCategory());
        });
    }

    // Tests fetching inventory report
    @Test
    public void testGet3() throws ApiException {
        Integer brandCategory = apiTestUtil.addBrand("brand", "category").getId();

        List<String> barcodeList = IntStream.range(0, 3).mapToObj(id -> ("barcode" + id)).collect(Collectors.toList());
        String name = "name";
        Double sellingPrice = 23.23;
        List<Integer> productIdList = apiTestUtil.addProductList(barcodeList, brandCategory, name, sellingPrice).stream().map(ProductPojo::getId).collect(Collectors.toList());

        List<Integer> quantityList = IntStream.range(3, 6).boxed().collect(Collectors.toList());
        List<InventoryPojo> inventoryPojoList = apiTestUtil.addInventoryList(productIdList, quantityList);

        Integer tot = 12;
        List< InventoryReportData> inventoryReportDataList = reportDto.getInventoryReportList();
        assertEquals("brand", inventoryReportDataList.get(0).getBrand());
        assertEquals("category", inventoryReportDataList.get(0).getCategory());
        assertEquals(tot, inventoryReportDataList.get(0).getQuantity());
    }

    // Tests fetching sales report
    @Test
    public void testGet4() throws ApiException {
        Integer brandId = apiTestUtil.addBrand("brand", "category").getId();
        Integer productId = apiTestUtil.addProduct("barcode", brandId, "name", 1.1).getId();
        Integer orderId = apiTestUtil.addOrder("order", OrderStatus.CREATED).getId();
        List<Integer> quantityList = IntStream.range(3, 6).boxed().collect(Collectors.toList());
        List<Double> sellingPrice = Stream.of(3, 4, 5).map(id -> id * 2.5).collect(Collectors.toList());

        IntStream.range(0, 3).forEach(id -> apiTestUtil.addOrderItem(orderId, productId, quantityList.get(id), sellingPrice.get(id)));
        OrderPojo o = apiTestUtil.placeOrder(orderId);

        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusDays(1);

        SalesReportForm salesReportForm = apiTestUtil.getSalesReportForm(startDate, endDate);
        List< SalesReportData> salesReportDataList = reportDto.getSalesReportList(salesReportForm);
        assertEquals(1, salesReportDataList.size());
    }


}
