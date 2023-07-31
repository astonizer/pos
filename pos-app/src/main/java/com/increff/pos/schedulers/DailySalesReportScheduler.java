package com.increff.pos.schedulers;

import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.OrderItemService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ReportService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@Log4j
public class DailySalesReportScheduler {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private OrderItemService orderItemService;

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailySalesReport() {
        ZonedDateTime startDate = ZonedDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endDate = ZonedDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        reportService.add(cr(startDate, endDate));

        log.info("The scheduler was executed.");
    }

    private DailySalesReportPojo cr(ZonedDateTime startDate, ZonedDateTime endDate) {
        int invoicedOrdersCount = 0, invoicedItemsCount = 0;
        double totalRevenue = 0;

        List<OrderPojo> orderPojoList = orderService.findInvoicedOrdersInRange(startDate, endDate);

        for(OrderPojo orderPojo: orderPojoList) {

            List<OrderItemPojo> orderItemPojoList = orderItemService.getOrderItemsByOrderId(orderPojo.getId());

            for(OrderItemPojo orderItemPojo: orderItemPojoList) {

                double totalSaleForItem = (orderItemPojo.getQuantity() * orderItemPojo.getSellingPrice());

                totalRevenue += totalSaleForItem;
                invoicedItemsCount++;
            }

            invoicedOrdersCount++;
        }

        return createDailySalesReportPojo(invoicedOrdersCount, invoicedItemsCount, totalRevenue);
    }

    private DailySalesReportPojo createDailySalesReportPojo(int invoicedOrdersCount, int invoicedItemsCount, double totalRevenue) {
        DailySalesReportPojo dailySalesReportPojo = new DailySalesReportPojo();

        dailySalesReportPojo.setInvoicedOrdersCount(invoicedOrdersCount);
        dailySalesReportPojo.setInvoicedItemsCount(invoicedItemsCount);
        dailySalesReportPojo.setTotalRevenue(totalRevenue);

        return dailySalesReportPojo;
    }

}
