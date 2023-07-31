package com.increff.pos.controller.api;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.model.data.*;
import com.increff.pos.model.form.SalesReportForm;
import com.increff.pos.service.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/api/reports")
public class ReportsApiController {

    @Autowired
    private ReportDto reportDto;

    @ApiOperation(value = "List the sales report")
    @PostMapping("/sales")
    public List<SalesReportData> getSalesReport(@RequestBody SalesReportForm salesReportForm) throws ApiException {
        return reportDto.getSalesReportList(salesReportForm);
    }

    @ApiOperation(value = "Lists the daily sales report")
    @GetMapping("/daily-sales")
    public List<DailySalesReportData> getDailySalesReport() throws ApiException {
        return reportDto.getDailySalesReportList();
    }

    @ApiOperation(value = "Lists all brands")
    @GetMapping("/brands")
    public List<BrandReportData> getBrandReport() throws ApiException {
        return reportDto.getBrandReportList();
    }

    @ApiOperation(value = "Lists inventory")
    @GetMapping("/inventory")
    public List<InventoryReportData> getInventoryReport() throws ApiException {
        return reportDto.getInventoryReportList();
    }

}
