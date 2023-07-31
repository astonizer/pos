package com.increff.pos.schedulers;

import com.increff.pos.dao.ReportDao;
import com.increff.pos.model.enums.OrderStatus;
import com.increff.pos.pojo.DailySalesReportPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DailySalesReportSchedulerTest extends AbstractUnitTest {

    @Autowired
    private DailySalesReportScheduler dailySalesReportScheduler;

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private ReportDao reportDao;

    // Tests if correct daily sales is persisted
    @Test
    public void testAdd1() {
        ZonedDateTime yesterdayTime = ZonedDateTime.now().minusDays(1).withHour(5).withMinute(30).withSecond(30).withNano(0),
                todayTime = ZonedDateTime.now();

        OrderPojo orderPojoInvoiced1 = apiTestUtil.addOrder("order1", OrderStatus.INVOICED, yesterdayTime),
                orderPojoCreated = apiTestUtil.addOrder("order2", OrderStatus.CREATED, yesterdayTime),
                orderPojoInvoiced2 = apiTestUtil.addOrder("order3", OrderStatus.CREATED, todayTime);

        apiTestUtil.addOrderItem(orderPojoInvoiced1.getId(), 1, 5, 10.0);
        apiTestUtil.addOrderItem(orderPojoCreated.getId(), 3, 2, 5.5);
        apiTestUtil.addOrderItem(orderPojoInvoiced1.getId(), 2, 1, 3.5);
        apiTestUtil.addOrderItem(orderPojoInvoiced2.getId(), 2, 1, 3.5);

        dailySalesReportScheduler.generateDailySalesReport();

        Integer orderCount = 1, itemCount = 2;
        Double tot = 53.5;
        List< DailySalesReportPojo> dailySalesReportPojoList = reportDao.getAll();
        assertEquals(1, dailySalesReportPojoList.size());
        assertEquals(orderCount, dailySalesReportPojoList.get(0).getInvoicedOrdersCount());
        assertEquals(itemCount, dailySalesReportPojoList.get(0).getInvoicedItemsCount());
        assertEquals(tot, dailySalesReportPojoList.get(0).getTotalRevenue());
    }

}
