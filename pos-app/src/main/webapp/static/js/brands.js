var isSupervisor = false;
var brandTable;
var role = null;
var initialBrandValue;
var initialCategoryValue;

function populateErrorData(errors) {
    $.each(fileData, function(key, value) {
        var row = {};
        for(id in expectedColumns) {
            row[expectedColumns[id]] = value[expectedColumns[id]];
        }
        row["error"] = errors[key] || " - ";
        row["brand"] = fileData[key].brand;
        row["category"] = fileData[key].category;
        errorData.push(row);
    });

    $("#download-errors").removeAttr("disabled");
}

// File upload variables
var fileData = [];
var errorData = [];
var expectedColumns = ['brand', 'category'];

function getBrandsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
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

function getBrand(id) {
    var url = getBrandsUrl() + '/' + id;
	return $.ajax({
        url: url,
        type: 'GET'
	});
}

function getBrandList() {
    var url = getBrandsUrl();
    return $.ajax({
        url: url,
        type: 'GET'
    });
}

// Main methods
function addBrand(event) {
    var $form = $("#brand-form");

    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

    var json = toJson($form);
    var url = getBrandsUrl();

    apiCall(url, json, 'POST').done(function(data) {
        handleSuccess("Brand creation was successful!");
        brandTable.draw();   // Update table
        toggleBrandModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });
}

function updateBrand(){
    var $form = $('#brand-form');

	//Get the ID
	var id = $("#brand-form input[name=id]").val();
	var url = getBrandsUrl() + "/" + id;
    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        handleFormError('Please fill all the required details correctly!');
        return false;
    }

	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);

	apiCall(url, json, 'PUT').done(function(data) {
        handleSuccess("Brand successfully updated!");
        brandTable.draw();   // Update table
        toggleBrandModal();
        resetForm();
    }).fail(function(error) {
        handleAjaxError(error);
    });

	return false;
}

function editBrand(id){
    getBrand(id).done(function(brandData) {
        createUpdateModal(brandData);
        disableAddButtonIfUnchanged();
    }).fail(handleAjaxError);
}

// Upload methods
function uploadData(){
	var json = JSON.stringify(fileData);
	var url = getBrandsUrl() + '/bulk';

	// Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
            brandTable.draw();   // Update table
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
    var file = $('#brand-file')[0].files[0];
    readFileData(file, readFileDataCallback);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-brand-modal').modal('toggle');
}

function refreshList() {
    var refreshButton = $('#refresh-data');
    var refreshIcon = $('#refresh-icon');
    var refreshText = $('#refresh-text');

    refreshButton.prop('disabled', true); // Disable the button during the refresh

    refreshIcon.addClass('spinner-border'); // Add the spinner class
    refreshText.text('');

    brandTable.draw();   // Update table

    // Simulate a delay of 1 second before resetting the button
    setTimeout(function() {
        refreshIcon.removeClass('spinner-border'); // Remove the spinner class
        refreshText.text('Refresh list');
        refreshButton.prop('disabled', false); // Enable the button
        $('#refresh-icon').hide();
    }, 1000);
}

function preprocessBrandList(response) {
    var pageLength = brandTable.page.len(); // Get the current page length
    var currentPage = brandTable.page.info().page; // Get the current page number

    return response.data.map(function(brand, index) {
        var modifiedBrand = {
            id: (currentPage * pageLength) + (index + 1), // Adjusted ID based on the page and page length
            brand: brand.brand,
            category: brand.category
        };

        if (isSupervisor) {
            var buttonDiv = '<button type="button" class="btn btn-warning" onclick="editBrand(' + brand.id + ')">Edit</button>';
            modifiedBrand.btn = buttonDiv;
        }

        return modifiedBrand;
    });
}

function disableAddButtonIfUnchanged() {
  // Get the initial values of the input fields
  initialBrandValue = $('#input-brand').val();
  initialCategoryValue = $('#input-category').val();

  // Add event listener to input fields
  $('#input-brand, #input-category').on('input', checkUpdateFunction);

   // Add the input event listener as a data attribute on the modal element
    $('#brand-modal').data('inputEventListener', checkUpdateFunction);
}

function checkUpdateFunction() {
    // Get the current values of the input fields
    var currentBrandValue = $('#input-brand').val();
    var currentCategoryValue = $('#input-category').val();

    // Enable/disable the "Add" button based on input field changes
    if (currentBrandValue === initialBrandValue && currentCategoryValue === initialCategoryValue) {
      $('#add-brand').prop('disabled', true);
    } else {
      $('#add-brand').prop('disabled', false);
    }
}

// DOM manipulations
function createNewModal() {
    resetForm();
    $('#brand-modal-title').text("Add Brand");
    $('#add-brand').text("Add");
    $('#add-brand').off('click').on('click', addBrand);
    toggleBrandModal();
}

function createUpdateModal(data) {
    resetForm();
    populateBrandForm(data);
    $('#brand-modal-title').text("Update Brand");
    $('#add-brand').text("Update");
    $('#add-brand').off('click').on('click', updateBrand);
    disableUpdateBrandButton();
    toggleBrandModal();
}

function toggleBrandModal() {
    $('#brand-modal').modal('toggle');
}

function disableUpdateBrandButton() {
    $("#add-brand").attr("disabled", "disabled");
}

function enableUpdateBrandButton() {
    $("#add-brand").removeAttr("disabled");
}

function resetForm() {
    $("#brand-form input[name=brand]").val("");
	$("#brand-form input[name=category]").val("");
	$("#brand-form input[name=id]").val("");
    $(".form-control").removeClass("is-invalid");
}

function populateBrandForm(data){
	$("#brand-form input[name=brand]").val(data.brand);
	$("#brand-form input[name=category]").val(data.category);
	$("#brand-form input[name=id]").val(data.id);
}

function updateFileName(){
	var $file = $('#brand-file');
	var fileName = $file.val().split("\\").pop();
	$('#brand-file-name').html(fileName);
	$('#process-data').prop('disabled', false);
}

function resetUploadDialog() {
	// Reset file name
	var $file = $('#brand-file');
	$file.val('');
	$('#brand-file-name').html("Choose a file");
	// Reset various counts
	fileData = [];
	errorData = [];

	$("#download-errors").attr("disabled", "disabled");
	$('#process-data').prop('disabled', true);
}

function getColumns() {
    var columns = [
        { data: 'id' },
        { data: 'brand' },
        { data: 'category' }
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
	    downloadErrors('errors-brands');
	});
	$('#brand-file').on('change', updateFileName);
	$('#brand-modal').on('hidden.bs.modal', function() {
        resetFormValidation($('#brand-form'));
    });

    brandTable = $('#brand-table').DataTable({
        paging: true,
        lengthMenu: [5, 10, 25, 50, 100],
        pageLength: 10,
        searching: false,
        ordering: false,
        serverSide: true,   // Enable server-side processing
        processing: true,   // Enable loader while loading data
        ajax: {
            url: getBrandsUrl(),
            type: 'GET',
            dataType: 'json',
            dataSrc: preprocessBrandList
        },
        columns: getColumns(),
        scrollX: true,
        sScrollXInner: "100%"
    });
    $('#brand-table_wrapper').addClass('m-5');

    $('#refresh-data').on('click', function() {
        $('#refresh-icon').show();
        refreshList();
  });

  $('#brand-modal').on('hidden.bs.modal', function() {
      // Get the input event listener from the data attribute
      var inputEventListener = $(this).data('inputEventListener');
      // Remove the input event listener from the input fields
      $('#input-brand, #input-category').off('input', inputEventListener);
      // Clear the "Add" button disabled state
      $('#add-brand').prop('disabled', false);
    });
}

$(document).ready(init);