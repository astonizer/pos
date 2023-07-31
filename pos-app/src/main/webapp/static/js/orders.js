var orderTable;
var currentOrder = [];
var orderName = "";
var orderItemTable;
var currentEditableIndex = -1;
var editing = false;
var orderId;
var orderStatus = null;

function getOrdersUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-items";
}

// Api methods
function apiCall(url, json, type) {
    return $.ajax({
        url: url,
        type: type,
        data: json,
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

function deleteItemCall(url) {
    return $.ajax({
        url: url,
        type: 'DELETE'
    });
}

function getOrderList() {
    var url = getOrdersUrl();
    return $.ajax({
        url: url,
        type: 'GET',
    });
}

function getOrderItemList(id) {
    var url = getOrdersUrl() + '/' + id;
    return $.ajax({
        url: url,
        type: 'GET',
    });
}

// post requests
function createOrder() {
    if(currentOrder.length == 0) {
//        $('#order-modal').modal('toggle');
//        resetModal();
        handleFormError("Order cannot be empty!");
        return;
    }

    currentOrder = currentOrder.filter(item => item.quantity !== 0);

    var json = getJsonData(currentOrder);
    var url = getOrdersUrl();

    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: json,
        success: function(data) {
            $('#order-modal').modal('toggle');
            currentOrder = data.orderItemDataList;
            resetModal();
            handleSuccess("Order creation was successful!", "success");
        },
        error: function(error) {
            handleAjaxError(error);
        }
    });
}

function updateOrder(id) {
    if(currentOrder.length == 0) {
        $('#order-modal').modal('toggle');
        resetModal();
        handleFormError("Order cannot be empty!");
        return;
    }

    var json = getJsonData(currentOrder);
    var url = getOrdersUrl() + '/' + id;

    $.ajax({
        url: url,
        type: 'PUT',
        contentType: 'application/json',
        data: json,
        success: function(data) {
            $('#order-modal').modal('toggle');
            currentOrder = data.orderItemDataList;
            resetModal();
            viewOrder(id, 'CREATED');
            handleSuccess("Successfully updated the order!", "success");
        },
        error: handleAjaxError
    });
}

function placeOrder(id) {
    if(editing) {
        handleFormError('Please save the changes first!');
        return;
    }
    var url = getOrdersUrl() + "/" + id + "/place";
    $.ajax({
        url: url,
        type: 'POST',
        xhrFields: {
            responseType: 'blob'
        },
        success: function(data) {
            $('#order-modal').modal('toggle');
            resetModal();
            handleSuccess('Order has been placed successfully!');
            orderTable.draw();

            // Create a temporary URL for the blob
            const url = URL.createObjectURL(data);

            // Create a link element
            const link = document.createElement('a');
            link.href = url;
            link.download = 'invoice_' + id + '.pdf';

            // Append the link to the document and trigger the download
            document.body.appendChild(link);
            link.click();

            // Clean up the temporary URL and link element
            URL.revokeObjectURL(url);
            document.body.removeChild(link);
      },
      error: function(xhr, status, error) {
            if(xhr.status === 400) {
                handleFormError("Order couldn't be invoiced, please try again later!")
            } else {
            console.error(error)
                handleAjaxError(error);
            }
      }
  });
}

function getInvoice(id) {
    var url = getOrdersUrl() + '/' + id + '/invoice';
    $.ajax({
        url: url,
        type: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function(data) {
            // Create a temporary URL for the blob
            const url = URL.createObjectURL(data);

            // Create a link element
            const link = document.createElement('a');
            link.href = url;
            link.download = 'invoice_' + id + '.pdf';

            // Append the link to the document and trigger the download
            document.body.appendChild(link);
            link.click();

            // Clean up the temporary URL and link element
            URL.revokeObjectURL(url);
            document.body.removeChild(link);
      },
      error: function(xhr, status, error) {
            if(xhr.status === 400) {
                handleFormError("Invoice couldn't be downloaded, please try again later!")
            } else {
                handleAjaxError(error);
            }
      }
    });
}

function addOrderItem() {
    var $form = $("#order-item-form");

    resetErrorStatus();

    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

    var json = JSON.parse(toJson($form));
    json.quantity = scientificNumberReviver(json.quantity);
    json.barcode = json.barcode.trim().toLowerCase();
    orderName = json.name;

    $('#input-barcode').val(json.barcode);

    var foundItem = currentOrder.find(function(item) {
        return item.barcode === json.barcode;
    });
    if(foundItem !== undefined) {
        handleFormError("An item with this barcode already exists. Please either edit the existing item or delete it before attempting to add the same item again.");
        return false;
    }

    var dataValue = $('#add-order-item').attr('status');

    var item = {
        barcode: json.barcode,
        quantity: parseInt(json.quantity),
        sellingPrice: parseFloat(json.sellingPrice).toFixed(2)
    };

    var url = getInventoryUrl() + '/check';
    if(orderStatus === 'CREATED') {
        url = getOrdersUrl() + '/' + orderId;
    }

    apiCall(url, JSON.stringify(item), 'POST').done(function(response) {
        item.total = parseFloat((item.quantity * item.sellingPrice).toFixed(2));
        item.isEditable = false;
        item.barcode = item.barcode.trim().toLowerCase();
        currentOrder.push(item);
        if(dataValue === 'NEW') {
            displayOrderItems();
         } else {
            displayOrderItemList(currentOrder, 'CREATED')
         }
         resetForm();
    }).fail(function(response) {
        var message = JSON.parse(response.responseText).message;
        handleAjaxError(response);
    });
}

function addData(event, status, id) {
    event.preventDefault();
    resetList();
    resetForm();
    $('#input-name').val('');

    var modalFooter = $('#modal-footer');
    modalFooter.empty();

    var cancelButton = $('<button>').attr('data-dismiss', 'modal').attr('id', 'cancel-changes').addClass('btn btn-danger').text('Cancel');
    modalFooter.append(cancelButton);
    $('#cancel-button').click(resetForm);

    if(typeof status === 'undefined') {
        $('#add-order-item').attr('status', 'NEW');
        $('#order-modal-title').text('New Order');
        var createButton = $('<button>').attr('id', 'create-order').addClass('btn btn-success').text('Create');

        modalFooter.append(createButton);
        displayOrderItems();
        if($('#order-item-table thead tr th').length < 6)  {
            var actionsHeader = $('<th scope="col">Actions</th>');

            // Append the new th element to the header row
            $('#order-item-table thead tr').append(actionsHeader);
        }
        $('#create-order').click(createOrder);
        if (!$('#order-modal').is(':visible')) {

        }
    } else if(status === 'CREATED') {
        $('#add-order-item').attr('status', 'CREATED');
        var updateButton = '<button type="button" class="btn btn-primary" id="update-button" onclick="updateOrder(' + id + ')">Update</button>';
        var confirmButton = '<button type="button" class="btn btn-success" id="confirm-button" onclick="confirmInvoice(\'' + status + '\', ' + id + ')">Invoice</button>';
        modalFooter.append(updateButton);
        modalFooter.append(confirmButton);
    } else if(status === 'INVOICED') {
        var invoiceButton = $('<button>').attr('id', 'get-invoice').addClass('btn btn-success').text('Invoice');

        modalFooter.append(invoiceButton);
        if($('#order-item-table thead tr th').length < 6)  {
            var actionsHeader = $('<th scope="col">Actions</th>');

            // Append the new th element to the header row
            $('#order-item-table thead tr').append(actionsHeader);
        }
        $('#get-invoice').click(function(event) {
            getInvoice(event, id);
        });
    }

    if (!$('#order-modal').is(':visible')) {
        $('#order-modal').modal('toggle');
    }
}

function confirmInvoice(status, id) {
    if (!$('#order-modal').is(':visible')) {
        viewOrder(id, status);
    }

    var modalFooter = $('#modal-footer');
    var footerContents = modalFooter.html();
    modalFooter.empty();

    var textSpan = '<h5 class="mr-3">Are you sure you want to proceed?</h5>';
    var cancelButton = $('<button>').attr('id', 'cancel-place-order').addClass('btn btn-danger').text('No');
    var proceedButton = $('<button>').attr('id', 'place-order').addClass('btn btn-success').text('Yes');
    modalFooter.append(textSpan);
    modalFooter.append(cancelButton);
    modalFooter.append(proceedButton);
    $('#place-order').click(function(event) {
        placeOrder(id);
    });
    $('#cancel-place-order').click(function(event) {
        // Restore the original footer content
        modalFooter.empty().append(footerContents);
    });
}

// Display methods
function displayOrder(id, status) {
    getOrderItemList(id).done(function(orderDetails) {
        $('#order-modal-title').text('Order #' + orderDetails.id);
        setOrderName(orderDetails.name);
        orderStatus = orderDetails.status;
        currentOrder = orderDetails.orderItemDataList;
        orderId = orderDetails.id;
        displayOrderItemList(currentOrder, status);
    }).fail(handleAjaxError);
}

// display created orders
function displayOrderItemList(orderItemList, status) {
    if(status === 'INVOICED') {
        $('#order-item-table thead tr th:last-child').remove();
    } else if($('#order-item-table thead tr th').length < 6)  {
        var actionsHeader = $('<th scope="col">Actions</th>');

        // Append the new th element to the header row
        $('#order-item-table thead tr').append(actionsHeader);
    }
    $('#order-item-table').find("tbody").empty();

    function compareByQuantity(item1, item2) {
      // If both items have a zero quantity, keep their original order
      if (item1.quantity === 0 && item2.quantity === 0) {
        return 0;
      }
      // If only item1 has zero quantity, move it to the end
      else if (item1.quantity === 0) {
        return 1;
      }
      // If only item2 has zero quantity, move it to the end
      else if (item2.quantity === 0) {
        return -1;
      }
      // Otherwise, keep the original order
      else {
        return 0;
      }
    }

    // Sort the data using the custom comparator function
    currentOrder.sort(compareByQuantity);

    var totalAmount = 0;

    currentOrder.forEach(function(rowData, i) {
        var rowData = orderItemList[i];
        var buttonDiv = '<div class="d-flex">';
        var quantityCellContent = rowData.quantity;
        var sellingPriceCellContent = rowData.sellingPrice;

        if(status === 'INVOICED' && rowData.quantity === 0) {
            return;
        }

        if(status === 'CREATED') {
            if(currentEditableIndex !== i) {
                buttonDiv += '<button onclick="editOrderItem(' + i + ', \'CREATED\')" type="button" class="btn btn-warning d-flex justify-content-center align-items-center" id="edit-order-item"' + (editing ? 'disabled' : '') + '>Edit</button>'
            } else {
                quantityCellContent = '<input id="quantity' + rowData.id + '" class="form-control" type="number" style="width: 100px;" step="1" min="1" max="100000000" value="' + rowData.quantity + '">';
                sellingPriceCellContent = '<input id="sellingPrice' + rowData.id + '" class="form-control" type="number" style="width: 100px;" step="0.01" min="0" max="100000000" value="' + rowData.sellingPrice + '">';
                buttonDiv += '<button onclick="saveOrderItem(' + rowData.id + ',\'' + rowData.barcode + '\',\'CREATED\', ' + i + ')" type="button" class="btn btn-primary d-flex justify-content-center align-items-center" id="save-order-item">Save</button>'
            }

            if(currentEditableIndex !== i) {
                buttonDiv += '<button onclick="deleteOrderItem(' + rowData.id + ', \'CREATED\', ' + i + ')" type="button" class="btn btn-danger ml-2 d-flex justify-content-center align-items-center"' + (editing ? 'disabled' : '') + '>Delete</button>'
            } else {
                buttonDiv += '<button onclick="cancelOrderItem(1)" type="button" class="ml-2 btn btn-danger d-flex justify-content-center align-items-center">Cancel</button>'
            }
        }
        buttonDiv += '</div>';

        var row = '<tr>'
        + '<td>' + parseInt(i+1) + '</td>'
        + '<td>' + rowData.barcode + '</td>'
        + '<td>' + quantityCellContent + '</td>'
        + '<td>' + sellingPriceCellContent + '</td>'
        + '<td>' + rowData.total.toFixed(2) + '</td>'
        + (status !== 'INVOICED' ? ('<td>' + buttonDiv + '</td>') : '')
        + '</tr>';

        $('#order-item-table').find("tbody").append(row);

        totalAmount += parseFloat(rowData.total);
    });

    updateTotalAmount(totalAmount);
}

// display when creating new order
function displayOrderItems() {
    $('#order-item-table').find("tbody").empty();


    function compareByQuantity(item1, item2) {
      // If both items have a zero quantity, keep their original order
      if (item1.quantity === 0 && item2.quantity === 0) {
        return 0;
      }
      // If only item1 has zero quantity, move it to the end
      else if (item1.quantity === 0) {
        return 1;
      }
      // If only item2 has zero quantity, move it to the end
      else if (item2.quantity === 0) {
        return -1;
      }
      // Otherwise, keep the original order
      else {
        return 0;
      }
    }

    // Sort the data using the custom comparator function
    currentOrder.sort(compareByQuantity);

        var totalAmount = 0;
        currentOrder.forEach(function(rowData, i) {
            var rowData = currentOrder[i];

            var buttonDiv = '<div class="d-flex">';
            var quantityCellContent = rowData.quantity;
            var sellingPriceCellContent = rowData.sellingPrice;

            if(currentEditableIndex !== i) {
                buttonDiv += '<button onclick="editOrderItem(' + i + ')" type="button" class="btn btn-warning d-flex justify-content-center align-items-center" id="edit-order-item"' + (editing ? 'disabled' : '') + '>Edit</button>'
            } else {
                quantityCellContent = '<input id="quantity' + i + '" class="form-control" type="number" style="width: 100px;" step="1" min="1" max="100000000" value="' + rowData.quantity + '">';
                sellingPriceCellContent = '<input id="sellingPrice' + i + '" class="form-control" type="number" style="width: 100px;" step="0.01" min="0" max="100000000" value="' + rowData.sellingPrice + '">';
                buttonDiv += '<button onclick="saveOrderItem(' + i + ',\'' + rowData.barcode + '\',\'NONE\', ' + i + ')" type="button" class="btn btn-primary d-flex justify-content-center align-items-center" id="save-order-item">Save</button>'
            }

            if(currentEditableIndex !== i) {
                buttonDiv += '<button onclick="deleteOrderItem(' + i + ', \'NONE\', ' + i +')" type="button" class="btn btn-danger ml-2 d-flex justify-content-center align-items-center"' + (editing ? 'disabled' : '') + '>Delete</button>'
            } else {
                buttonDiv += '<button onclick="cancelOrderItem(0)" type="button" class="ml-2 btn btn-danger d-flex justify-content-center align-items-center">Cancel</button>'
            }

            buttonDiv += '</div>';

            var row = '<tr>'
            + '<td>' + parseInt(i+1) + '</td>'
            + '<td>' + rowData.barcode + '</td>'
            + '<td>' + quantityCellContent + '</td>'
            + '<td>' + sellingPriceCellContent + '</td>'
            + '<td>' + rowData.total.toFixed(2) + '</td>'
            + '<td>' + buttonDiv + '</td>'
            + '</tr>';

            $('#order-item-table').find("tbody").append(row);

            totalAmount += parseFloat(rowData.total);
        });

        updateTotalAmount(totalAmount);
}

function updateTotalAmount(totalAmount) {
    $('.order-total td:nth-last-child(2)').text(totalAmount.toFixed(2));
}

function preprocessOrderList(response) {
    var pageLength = orderTable.page.len(); // Get the current page length
    var currentPage = orderTable.page.info().page; // Get the current page number

    return response.data.map(function(order, index) {
        var status = 0;
        if(order.status === 'INVOICED') {
            status = 1;
        }

        var buttonDiv = '<div class=""><div class="row">';
        var statusBadge = '<span class="badge badge-info" style="font-size: 0.9em">INVOICED</span>';

        if (order.status === 'INVOICED') {
            buttonDiv += '<div class="col"><button onclick="getInvoice(' + order.id + ')" type="button" class="btn btn-primary">Download Invoice</button></div>';
        } else if(order.status === 'CREATED') {
            buttonDiv += '<div><button onclick="confirmInvoice(\'CREATED\', ' + order.id + ')" type="button" class="btn btn-success">Invoice Order</button></div>';
            statusBadge = '<span class="badge badge-warning" style="font-size: 0.9em">CREATED</span>';
        }
        buttonDiv += '</div></div>';

        var indexDiv = '<span onclick="viewOrder(' + order.id + ', \'' + order.status + '\')" class="index-button index-button-active' + '">' + ((currentPage * pageLength) + (index + 1)) + '</span>';

        var modifiedOrder = {
            id: indexDiv, // Adjusted ID based on the page and page length
            name: order.name,
            createdAt: formatToDateAndTime(order.createdAt * 1000),
            status: statusBadge,
            btn: buttonDiv
        };

        return modifiedOrder;
    });
}

function viewOrder(id, status) {
    displayOrder(id, status);
    addData(event, status, id)
}

function getJsonData(itemList) {
    var orderForm = {
        name: $('#input-name').val(),
        orderItemFormList: JSON.parse(JSON.stringify(itemList))
    }
    return JSON.stringify(orderForm);
}

function setOrderName(orderName) {
    $("#order-item-form input[name=name]").val(orderName);
}

function checkIfInteger() {
    var quantityValue = parseFloat($('#input-quantity').val());
    if (quantityValue !== Math.round(quantityValue)) {
      return false;
    }
    return true;
}

function deleteOrderItem(id, status, i) {
    if(status === 'CREATED') {
        var url = getOrderItemUrl() + '/' + id;
        deleteItemCall(url).done(function(response) {
            currentOrder[i].quantity = 0;
            currentOrder[i].total = 0.00;
            displayOrderItemList(currentOrder, status);
        }).fail(function(error) {
            handleAjaxError(error);
        });
     } else {
        currentOrder[i].quantity = 0;
        currentOrder[i].total = 0.00;
        displayOrderItems();
     }
}

function editOrderItem(id, status) {
    if(currentEditableIndex === -1) {
        currentEditableIndex = id;
        $('#create-order').attr('disabled', true);
        editing = true;
    } else {
        handleFormError("Please save the changes before editing another entry!");
    }
    if(status === 'CREATED')
        displayOrderItemList(currentOrder, status);
    else
        displayOrderItems();
}

function saveOrderItem(id, barcode, status, i) {
    var quantity = $('#quantity' + id).val();
    var sellingPrice = $('#sellingPrice' + id).val();
    var item = {
        orderId: orderId,
        barcode: barcode,
        quantity: parseInt(quantity),
        sellingPrice: parseFloat(sellingPrice)
    };

    var url = getInventoryUrl() + '/check';
    var type = 'POST';
    if(status === 'CREATED') {
        url = getOrderItemUrl() + '/' + id;
        type = 'PUT'
    }

    apiCall(url, JSON.stringify(item), type).done(function(response) {
        item.total = parseFloat((item.quantity * item.sellingPrice).toFixed(2));
        item.isEditable = false;
        $('#create-order').attr('disabled', false);

        currentEditableIndex = -1;
        editing = false;

        if(status === 'CREATED') {
            currentOrder[i].quantity = quantity;
            currentOrder[i].sellingPrice = parseFloat(sellingPrice).toFixed(2);
            displayOrderItemList(currentOrder, status);
        } else {
            currentOrder[i].quantity = quantity;
            currentOrder[i].sellingPrice = parseFloat(sellingPrice).toFixed(2);
            displayOrderItems();
        }
    }).fail(function(response) {
        handleAjaxError(response);
    });
}

function cancelOrderItem(status) {
    currentEditableIndex = -1;
    editing = false;
    if(status === 1) displayOrderItemList(currentOrder, 'CREATED');
    else displayOrderItems();
    $('#create-order').attr('disabled', false);
}

function refreshList() {
    var refreshButton = $('#refresh-data');
    var refreshIcon = $('#refresh-icon');
    var refreshText = $('#refresh-text');

    refreshButton.prop('disabled', true); // Disable the button during the refresh

    refreshIcon.addClass('spinner-border'); // Add the spinner class
    refreshText.text('');

    orderTable.draw();

    // Simulate a delay of 1 second before resetting the button
    setTimeout(function() {
        refreshIcon.removeClass('spinner-border'); // Remove the spinner class
        refreshText.text('Refresh');
        refreshButton.prop('disabled', false); // Enable the button
        $('#refresh-icon').hide();
    }, 1000);
}

function resetForm() {
    $("#order-item-form input[name=barcode]").val("");
    $("#order-item-form input[name=quantity]").val("");
    $("#order-item-form input[name=sellingPrice]").val("");
    $("#order-item-form").removeClass("was-validated");
    currentEditableIndex = -1;
    editing = false;
}

function resetErrorStatus() {
    $('#input-barcode').removeClass('is-invalid');
    $('#input-quantity').removeClass('is-invalid');
}

function resetList() {
    currentOrder = [];
    orderTable.draw();
}

function resetModal() {
    orderName = "";
    $("#order-item-form input[name=name]").val(orderName);
    resetForm();
    resetList();
}

function getOrderColumns() {
    var columns = [
        { data: 'id' },
        { data: 'name' },
        { data: 'createdAt' },
        { data: 'status' },
        { data: 'btn' }
    ];

    return columns;
}

function init() {
    $('#add-data').click(addData);
    $('#add-order-item').click(addOrderItem);
    $('#refresh-data').click(refreshList);
    $('#close-modal').click(resetForm);

    orderTable = $('#order-table').DataTable({
        paging: true,
        lengthMenu: [5, 10, 25, 50, 100],
        pageLength: 10,
        searching: false,
        ordering: false,
        serverSide: true,   // Enable server-side processing
        processing: true,   // Enable loader while loading data
        ajax: {
            url: getOrdersUrl(),
            type: 'GET',
            dataType: 'json',
            dataSrc: preprocessOrderList
        },
        columns: getOrderColumns(),
        columnDefs: [
            { className: 'd-flex justify-content-center align-items-center', targets: [4] }
        ],
        scrollX: true,
        sScrollXInner: "100%"
    });
    $('#order-table_wrapper').addClass('m-5');

    $('#refresh-data').on('click', function() {
        $('#refresh-icon').show();
        refreshList();
  });

  $('#order-modal').on('hidden.bs.modal', function() {
    orderStatus = null;
  });
}

$(document).ready(init);