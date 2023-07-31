function getBrandsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/brands";
}

function getInventoryReportsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/inventory";
}

function getDailySalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/reports/daily-sales";
}

function getSalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/reports/sales";
}

function getBrandList() {
    var url = getBrandsUrl();
    return $.ajax({
           url: url,
           type: 'GET',
    });
}

function getInventoryList() {
    var url = getInventoryReportsUrl();
    return $.ajax({
           url: url,
           type: 'GET',
    });
}

function getDailySalesReportList() {
    var url = getDailySalesReportUrl();
    return $.ajax({
           url: url,
           type: 'GET'
    });
}

function getFilteredList(data) {
    var url = getSalesUrl();
    return $.ajax({
        url: url,
        type: 'POST',
        data: data,
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

function downloadBrandReport() {
    $('#download-brand-report').prop('disabled', true);
    $('#download-brand-report').text('Downloading...');
    getBrandList().done(function(brandList) {
        var reportList = [];
        for (var i = 0; i < brandList.length; i++) {
            var data = brandList[i];
            reportList.push({
                'Brand': data.brand,
                'Category': data.category
            });
        }
        writeFileData(reportList, 'csv', 'brand-report');
        $('#download-brand-report').prop('disabled', false);
        $('#download-brand-report').text('Download Report');
    }).fail(function(error) {
        handleAjaxError(error);
        $('#download-brand-report').prop('disabled', false);
        $('#download-brand-report').text('Download Report');
    });
}

function downloadInventoryReport() {
    $('#download-inventory-report').prop('disabled', true);
    $('#download-inventory-report').text('Downloading...');
    getInventoryList().done(function(inventoryList) {
        var reportList = [];
        for (var i = 0; i < inventoryList.length; i++) {
            var data = inventoryList[i];
            reportList.push({
                'Brand': data.brand,
                'Category': data.category,
                'Quantity': data.quantity
            });
        }
        writeFileData(reportList, 'csv', 'inventory-report');
        $('#download-inventory-report').prop('disabled', false);
        $('#download-inventory-report').text('Download Report');
    }).fail(function(error) {
        handleAjaxError(error);
        $('#download-inventory-report').prop('disabled', false);
        $('#download-inventory-report').text('Download Report');
    });
}

function downloadDailySalesReport() {
    $('#download-daily-sales-report').prop('disabled', true);
    $('#download-daily-sales-report').text('Downloading...');
    getDailySalesReportList().done(function(dailySalesReportList) {
        var reportList = [];
        for (var i = 0; i < dailySalesReportList.length; i++) {
            var data = dailySalesReportList[i];
            var date = new Date(data.date * 1000);

            reportList.push({
                'Date': formatToDate(date),
                'No. of Invoiced Orders': data.invoicedOrdersCount,
                'No. of Invoiced Items': data.invoicedItemsCount,
                'Total Revenue': data.totalRevenue
            });
        }
        writeFileData(reportList, 'csv', 'daily-sales-report');
        $('#download-daily-sales-report').prop('disabled', true);
        $('#download-daily-sales-report').text('Download Report');
    }).fail(function(error) {
        handleAjaxError(error);
        $('#download-daily-sales-report').prop('disabled', true);
        $('#download-daily-sales-report').text('Download Report');
    });
}

function downloadSalesReport(event) {
    $('#download-sales-report').prop('disabled', true);
    $('#download-sales-report').text('Downloading...');
    event.preventDefault();

    var $form = $('#sales-report-form');
    if (!validateFormInputs()) {
        return false;
    }

    var brand = $('#inputBrand').val();
    var category = $('#inputCategory').val();

    var startDate = new Date($("#inputStartDate").val());
    startDate.setHours(0);
    startDate.setMinutes(0);
    startDate.setSeconds(0);
    var endDate = new Date($("#inputEndDate").val());
    endDate.setHours(23);
    endDate.setMinutes(59);
    endDate.setSeconds(59);

    var data = {
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString(),
        brand: brand,
        category: category
    };

    var json = JSON.stringify(data);

    getFilteredList(json).done(function(salesReportList) {
        var reportList = [];
        for (var i = 0; i < salesReportList.length; i++) {
            var data = salesReportList[i];
            reportList.push({
                'Brand': data.brand,
                'Category': data.category,
                'Quantity': data.quantity,
                'Revenue': data.revenue
            });
        }

        writeFileData(reportList, 'csv', 'sales-report');
        $('#download-sales-report').prop('disabled', false);
        $('#download-sales-report').text('Download Report');
    }).fail(function(error) {
        handleAjaxError(error);
        $('#download-sales-report').prop('disabled', false);
        $('#download-sales-report').text('Download Report');
    });
}

function validateFormInputs() {
    var startDateInput = document.getElementById('inputStartDate');
    var endDateInput = document.getElementById('inputEndDate');

    if (startDateInput.checkValidity() && endDateInput.checkValidity()) {
        startDateInput.classList.add('is-valid');
        startDateInput.classList.remove('is-invalid');
        endDateInput.classList.add('is-valid');
        endDateInput.classList.remove('is-invalid');
        return true;
    } else {
        startDateInput.classList.add('is-invalid');
        startDateInput.classList.remove('is-valid');
        endDateInput.classList.add('is-invalid');
        endDateInput.classList.remove('is-valid');
        return false;
    }
}

function init() {
    $('#download-brand-report').click(downloadBrandReport);
    $('#download-inventory-report').click(downloadInventoryReport);
    $('#download-daily-sales-report').click(downloadDailySalesReport);
    $('#sales-report-form').submit(downloadSalesReport);
}

$(document).ready(init);