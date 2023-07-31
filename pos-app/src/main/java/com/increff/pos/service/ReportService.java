package com.increff.pos.service;

import com.increff.pos.dao.ReportDao;
import com.increff.pos.pojo.DailySalesReportPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ReportService {
    
    @Autowired
    private ReportDao reportDao;

    public List<DailySalesReportPojo> getDailySalesReport() {
        return reportDao.getAll();
    }

    public void add(DailySalesReportPojo dailySalesReportPojo) {
        reportDao.save(dailySalesReportPojo);
    }

}
