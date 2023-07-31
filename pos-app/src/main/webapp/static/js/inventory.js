var isSupervisor = false;
var inventoryTable;
var role = null;

function populateErrorData(errors) {
    $.each(fileData, function(key, value) {
        var row = {};
        for(id in expectedColumns) {
            row[expectedColumns[id]] = value[expectedColumns[id]];
        }
        row["error"] = errors[key] || " - ";
        row["barcode"] = fileData[key].barcode;
        row["quantity"] = fileData[key].quantity;
        errorData.push(row);
    });

    $("#download-errors").removeAttr("disabled");
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var expectedColumns = ['barcode', 'quantity'];

function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function getRole() {
    return $("meta[name=role]").attr("content");
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

function getInventory(id) {
    var url = getInventoryUrl() + '/' + id;
	return $.ajax({
        url: url,
        type: 'GET'
	});
}

function getInventoryList() {
    var url = getInventoryUrl();
    return $.ajax({
        url: url,
        type: 'GET'
    });
}

// Main methods
function addInventory(event) {
    var $form = $("#inventory-form");

    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

    var json = toJson($form);
    var url = getInventoryUrl();
    var tempJson = JSON.parse(json);
    tempJson.quantity = scientificNumberReviver(tempJson.quantity);
    json = JSON.stringify(tempJson);

    apiCall(url, json, 'POST').done(function(data) {
        handleSuccess("Inventory creation was successful!");
        inventoryTable.draw();
        toggleInventoryModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });
}

function updateInventory(){
	//Get the ID
	var id = $("#inventory-form input[name=id]").val();
	var url = getInventoryUrl() + "/" + id;

	//Set the values to update
	var $form = $("#inventory-form");
	var json = toJson($form);

    var tempJson = JSON.parse(json);
    tempJson.quantity = scientificNumberReviver(tempJson.quantity);
    json = JSON.stringify(tempJson);

	if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

	apiCall(url, json, 'PUT').done(function(data) {
        handleSuccess("Inventory successfully updated!");
        inventoryTable.draw();
        toggleInventoryModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });

	return false;
}

function editInventory(id){
    getInventory(id).done(function(inventoryData) {
        createUpdateModal(inventoryData);
        disableAddButtonIfUnchanged();
    }).fail(handleAjaxError);
}

// Upload methods
function uploadData(){
	var json = JSON.stringify(fileData);
	var url = getInventoryUrl() + '/bulk';

	// Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        inventoryTable.draw();
	        handleSuccess('File successfully uploaded!');
	        displayUploadData();
	        uploadIconUnanimate();
	   },
	   error: function(response) {
            handleUploadError(response);
	        $('#download-errors').prop('disabled', false);
            uploadIconUnanimate();
	   }
	});

}

function processData() {
    uploadIconAnimate();
    var file = $('#inventory-file')[0].files[0];
    readFileData(file, readFileDataCallback);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-inventory-modal').modal('toggle');
}

function refreshList() {
    var refreshButton = $('#refresh-data');
    var refreshIcon = $('#refresh-icon');
    var refreshText = $('#refresh-text');

    refreshButton.prop('disabled', true); // Disable the button during the refresh

    refreshIcon.addClass('spinner-border'); // Add the spinner class
    refreshText.text('');

    inventoryTable.draw();

    // Simulate a delay of 1 second before resetting the button
    setTimeout(function() {
        refreshIcon.removeClass('spinner-border'); // Remove the spinner class
        refreshText.text('Refresh list');
        refreshButton.prop('disabled', false); // Enable the button
        $('#refresh-icon').hide();
    }, 1000);
}

function preprocessInventoryList(response) {
    var pageLength = inventoryTable.page.len(); // Get the current page length
    var currentPage = inventoryTable.page.info().page; // Get the current page number

    return response.data.map(function(inventory, index) {
        var modifiedInventory = {
            id: (currentPage * pageLength) + (index + 1), // Adjusted ID based on the page and page length
            barcode: inventory.barcode,
            quantity: inventory.quantity
        };

        if (isSupervisor) {
            var buttonDiv = '<button type="button" class="btn btn-warning" onclick="editInventory(' + inventory.productId + ')">Edit</button>';
            modifiedInventory.btn = buttonDiv;
        }

        return modifiedInventory;
    });
}

function disableAddButtonIfUnchanged() {
  // Get the initial values of the input fields
  var initialQuantityValue = $('#input-quantity').val();

  // Add event listener to input fields
  $('#input-brand, #input-category').on('input', function() {
    // Get the current values of the input fields
    var currentQuantityValue = $('#input-quantity').val();

    // Enable/disable the "Add" button based on input field changes
    if (currentQuantityValue === initialQuantityValue) {
      $('#add-inventory').prop('disabled', true);
    } else {
      $('#add-inventory').prop('disabled', false);
    }
  });
}

// DOM manipulations
function createNewModal() {
    resetForm();
    enableFields();
    $('#inventory-modal-title').text("Add Inventory");
    $('#add-inventory').text("Add");
    $('#add-inventory').off('click').on('click', addInventory);
    toggleInventoryModal();
}

function createUpdateModal(data) {
    resetForm();
    populateInventoryForm(data);
    disableFields();
    $('#inventory-modal-title').text("Update Inventory");
    $('#add-inventory').text("Update");
    $('#add-inventory').off('click').on('click', updateInventory);
    disableUpdateInventoryButton();
    toggleInventoryModal();
}

function toggleInventoryModal() {
    $('#inventory-modal').modal('toggle');
}

function disableUpdateInventoryButton() {
    $("#add-brand").attr("disabled", "disabled");
}

function enableUpdateInventoryButton() {
    $("#add-brand").removeAttr("disabled");
}

function disableFields() {
    // Disable barcode, brand, and category fields
    $('#input-barcode').prop('disabled', true);
}

function enableFields() {
    // Disable barcode, brand, and category fields
    $('#input-barcode').prop('disabled', false);
}

function resetForm() {
    $("#inventory-form input[name=barcode]").val("");
	$("#inventory-form input[name=quantity]").val("");
	$("#inventory-form input[name=id]").val("");
    $(".form-control").removeClass("is-invalid");
    $('.help-text').show();
}

function populateInventoryForm(data){
	$("#inventory-form input[name=barcode]").val(data.barcode);
	$("#inventory-form input[name=quantity]").val(data.quantity);
	$("#inventory-form input[name=id]").val(data.productId);
}

function updateFileName(){
	var $file = $('#inventory-file');
	var fileName = $file.val().split("\\").pop();
	$('#inventory-file-name').html(fileName);
	$('#process-data').prop('disabled', false);
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#inventory-file');
	$file.val('');
	$('#inventory-file-name').html("Choose a file");
	// Reset various counts
	fileData = [];
	errorData = [];

	$("#download-errors").attr("disabled", "disabled");
	$('#process-data').prop('disabled', true);
}

function getColumns() {
    var columns = [
        { data: 'id' },
        { data: 'barcode' },
        { data: 'quantity' }
    ];

    if(isSupervisor) {
        columns.push({ data: 'btn' });
    }

    return columns;
}

// Initializer
function init() {
    role = getRole();
    isSupervisor = (role === 'SUPERVISOR');

    $('#add-data').click(createNewModal);
	$('#refresh-data').click(refreshList);
	$('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
	$('#download-errors').click(function(event) {
	    downloadErrors('errors-inventory');
	});
	$('#inventory-file').on('change', updateFileName);
	$('#inventory-modal').on('hidden.bs.modal', function() {
        resetFormValidation($('#inventory-form'));
    });

    var targets = [];
    if(isSupervisor) {
        targets = [3];
    }

    inventoryTable = $('#inventory-table').DataTable({
        paging: true,
        lengthMenu: [5, 10, 25, 50, 100],
        pageLength: 10,
        searching: false,
        ordering: false,
        serverSide: true,   // Enable server-side processing
        processing: true,   // Enable loader while loading data
        ajax: {
            url: getInventoryUrl(),
            type: 'GET',
            dataType: 'json',
            dataSrc: preprocessInventoryList
        },
        columns: getColumns(),
        scrollX: true,
        sScrollXInner: "100%"
    });
    $('#inventory-table_wrapper').addClass('m-5');

    inventoryTable.draw();

    $('#refresh-data').on('click', function() {
        $('#refresh-icon').show();
        refreshList();
    });

    $("#input-quantity").on("input", function() {
        var mrpInput = $(this);
        var invalidFeedback = mrpInput.next(".invalid-feedback");
        var helpText = mrpInput.siblings("#mrpHelp");

        if (mrpInput.is(":invalid")) {
            // If input is invalid, hide the help text and show the invalid feedback
            invalidFeedback.show();
            helpText.hide();
        } else {
            // If input is valid, show the help text and hide the invalid feedback
            invalidFeedback.hide();
            helpText.show();
        }
    });
}

$(document).ready(init);