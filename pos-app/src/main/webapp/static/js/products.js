var isSupervisor = false;
var productTable;
var role = null;

function populateErrorData(errors) {
    $.each(fileData, function(key, value) {
        var row = {};
        for(id in expectedColumns) {
            row[expectedColumns[id]] = value[expectedColumns[id]];
        }
        row["error"] = errors[key] || " - ";
        row["barcode"] = fileData[key].barcode;
        row["brand"] = fileData[key].brand;
        row["category"] = fileData[key].category;
        row["name"] = fileData[key].name;
        row["mrp"] = fileData[key].mrp;
        errorData.push(row);
    });

    $("#download-errors").removeAttr("disabled");
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var expectedColumns = ['barcode', 'brand', 'category', 'name', 'mrp'];

function getProductsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
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

function getProduct(id) {
    var url = getProductsUrl() + '/' + id;
	return $.ajax({
        url: url,
        type: 'GET'
	});
}

function getProductList() {
    var url = getProductsUrl();
    return $.ajax({
        url: url,
        type: 'GET'
    });
}

// Main methods
function addProduct(event) {
    var $form = $("#product-form");

    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

    var json = toJson($form);
    var url = getProductsUrl();

    apiCall(url, json, 'POST').done(function(data) {
        handleSuccess("Product creation was successful!");
        productTable.draw();
        toggleProductModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });
}

function updateProduct(){
    var $form = $('#product-form');

	//Get the ID
	var id = $("#product-form input[name=id]").val();
	var url = getProductsUrl() + "/" + id;

    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

	//Set the values to update
	var $form = $("#product-form");
	var json = toJson($form);

	var tempJson = JSON.parse(json);
    var data = JSON.stringify(tempJson);

	apiCall(url, data, 'PUT').done(function(data) {
        handleSuccess("Product successfully updated!");
        productTable.draw();
        toggleProductModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });

	return false;
}

function editProduct(id){
    getProduct(id).done(function(productData) {
        createUpdateModal(productData);
        disableAddButtonIfUnchanged();
    }).fail(handleAjaxError);
}

// Upload methods
function uploadData(){
	var json = JSON.stringify(fileData);
	var url = getProductsUrl() + '/bulk';

	// Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        productTable.draw();
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
    var file = $('#product-file')[0].files[0];
    readFileData(file, readFileDataCallback);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function refreshList() {
    var refreshButton = $('#refresh-data');
    var refreshIcon = $('#refresh-icon');
    var refreshText = $('#refresh-text');

    refreshButton.prop('disabled', true); // Disable the button during the refresh

    refreshIcon.addClass('spinner-border'); // Add the spinner class
    refreshText.text('');

    productTable.draw();

    // Simulate a delay of 1 second before resetting the button
    setTimeout(function() {
        refreshIcon.removeClass('spinner-border'); // Remove the spinner class
        refreshText.text('Refresh list');
        refreshButton.prop('disabled', false); // Enable the button
        $('#refresh-icon').hide();
    }, 1000);
}

function preprocessProductList(response) {
    var pageLength = productTable.page.len(); // Get the current page length
    var currentPage = productTable.page.info().page; // Get the current page number

    return response.data.map(function(product, index) {
        var modifiedProduct = {
            id: (currentPage * pageLength) + (index + 1), // Adjusted ID based on the page and page length
            barcode: product.barcode,
            brand: product.brand,
            category: product.category,
            name: product.name,
            mrp: product.mrp.toFixed(2)
        };

        if (isSupervisor) {
            var buttonDiv = '<button type="button" class="btn btn-warning" onclick="editProduct(' + product.id + ')">Edit</button>';
            modifiedProduct.btn = buttonDiv;
        }

        return modifiedProduct;
    });
}

function disableAddButtonIfUnchanged() {
  // Get the initial values of the input fields
  var initialNameValue = $('#input-name').val();
  var initialMrpValue = $('#input-mrp').val();

  // Add event listener to input fields
  $('#input-name, #input-mrp').on('input', function() {
    // Get the current values of the input fields
    var currentNameValue = $('#input-name').val();
    var currentMrpValue = $('#input-mrp').val();

    // Enable/disable the "Add" button based on input field changes
    if (currentNameValue === initialNameValue && currentMrpValue === initialMrpValue) {
      $('#add-product').prop('disabled', true);
    } else {
      $('#add-product').prop('disabled', false);
    }
  });
}

// DOM manipulations
function createNewModal() {
    resetForm();
    enableFields();
    $('#product-modal-title').text("Add Product");
    $('#add-product').text("Add");
    $('#add-product').off('click').on('click', addProduct);
    toggleProductModal();
}

function createUpdateModal(data) {
    resetForm();
    populateProductForm(data);
    disableFields();
    $('#product-modal-title').text("Update Product");
    $('#add-product').text("Update");
    $('#add-product').off('click').on('click', updateProduct);
    disableUpdateProductButton();
    toggleProductModal();
}

function toggleProductModal() {
    $('#product-modal').modal('toggle');
}

function disableUpdateProductButton() {
    $("#add-product").attr("disabled", "disabled");
}

function enableUpdateProductButton() {
    $("#add-product").removeAttr("disabled");
}

function disableFields() {
    // Disable barcode, brand, and category fields
    $('#input-barcode, #input-brand, #input-category').prop('disabled', true);
}

function enableFields() {
    // Disable barcode, brand, and category fields
    $('#input-barcode, #input-brand, #input-category').prop('disabled', false);
}

function resetForm() {
    $("#product-form input[name=barcode]").val("");
	$("#product-form input[name=brand]").val("");
	$("#product-form input[name=category]").val("");
	$("#product-form input[name=name]").val("");
	$("#product-form input[name=mrp]").val("");
	$("#product-form input[name=id]").val("");
    $(".form-control").removeClass("is-invalid");
    $('.help-text').show();
}

function populateProductForm(data){
	$("#product-form input[name=barcode]").val(data.barcode);
	$("#product-form input[name=brand]").val(data.brand);
	$("#product-form input[name=category]").val(data.category);
	$("#product-form input[name=name]").val(data.name);
	$("#product-form input[name=mrp]").val(data.mrp);
	$("#product-form input[name=id]").val(data.id);
}

function updateFileName(){
	var $file = $('#product-file');
	var fileName = $file.val().split("\\").pop();
	$('#product-file-name').html(fileName);
	$('#process-data').prop('disabled', false);
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#product-file');
	$file.val('');
	$('#product-file-name').html("Choose a file");
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
        { data: 'brand' },
        { data: 'category' },
        { data: 'name' },
        { data: 'mrp' }
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
	    downloadErrors('errors-products');
	});
	$('#product-file').on('change', updateFileName);
	$('#product-modal').on('hidden.bs.modal', function() {
        resetFormValidation($('#product-form'));
    });

    productTable = $('#product-table').DataTable({
        paging: true,
        lengthMenu: [5, 10, 25, 50, 100],
        pageLength: 10,
        searching: false,
        ordering: false,
        serverSide: true,   // Enable server-side processing
        processing: true,   // Enable loader while loading data
        ajax: {
            url: getProductsUrl(),
            type: 'GET',
            dataType: 'json',
            dataSrc: preprocessProductList
        },
        columns: getColumns(),
        scrollX: true,
        sScrollXInner: "100%"
    });
    $('#product-table_wrapper').addClass('m-5');

    $('#refresh-data').on('click', function() {
        $('#refresh-icon').show();
        refreshList();
    });

    $("#input-mrp").on("input", function() {
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