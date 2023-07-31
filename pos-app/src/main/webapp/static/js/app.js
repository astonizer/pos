//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
//    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}

function handleAjaxError(response){
    toastr.clear();
	var parsedResponse = JSON.parse(response.responseText);
    toastr.error(parsedResponse.message, 'Error', {
        timeOut: 36000,
        positionClass: 'toast-top-right',
        closeOnHover: false,
        closeButton: true,
    });
}

function handleFormError(message) {
    toastr.clear();
	toastr.error(message, 'Error', {
        timeOut: 36000,
        positionClass: 'toast-top-right',
        closeOnHover: false,
        closeButton: true,
    });
}

function handleSuccess(message) {
    toastr.clear();
	toastr.success(message, 'Success', {
        timeOut: 5000,
        positionClass: 'toast-top-right',
        progressBar: true,
        closeOnHover: false,
        closeButton: true,
    });
}

function uploadIconAnimate() {
    var uploadButton = $('#process-data');
    var uploadIcon = $('#process-icon');
    var uploadText = $('#process-text');
    uploadIcon.show();

    uploadButton.prop('disabled', true); // Disable the button during the refresh

    uploadIcon.addClass('spinner-border'); // Add the spinner class
    uploadText.text('');
}

function uploadIconUnanimate() {
    $('#process-icon').removeClass('spinner-border'); // Remove the spinner class
    $('#process-text').text('Upload');
    $('#process-data').prop('disabled', false); // Enable the button
    $('#process-icon').hide();
}

function readFileData(file, callback){
    if(file === undefined) {
        // Handle empty file or invalid format
        handleFormError('Please upload a file!');
        uploadIconUnanimate();
        return;
    }
    if(!file.name.endsWith('.tsv')) {
        uploadIconUnanimate();
        handleFormError('Please upload a tsv file!');
        return false;
    }

	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}
	}
	Papa.parse(file, config);
}

function writeFileData(arr, format, fileName) {
    var config = {
        quoteChar: '"',
        escapeChar: '"',
        delimiter: ","
    };
    var data = Papa.unparse(arr, config);
    var mimeType = format === 'csv' ? 'text/csv' : 'text/tsv';
    var fileExtension = format === 'csv' ? 'csv' : 'tsv';
    var blob = new Blob([data], { type: mimeType + ';charset=utf-8;' });
    var fileUrl = null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, fileName + '.' + fileExtension);
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }

    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', fileName + '.' + fileExtension);
    tempLink.click();
}

function readFileDataCallback(results){
    if (results.length === 0) {
        // Handle empty file or invalid format
        handleFormError('Please upload a file!');
        uploadIconUnanimate();
        return;
    }

    if(results.data.length === 0) {
        handleFormError("File doesn't have any data to upload!");
        uploadIconUnanimate();
        return false;
    }

    var columns = Object.keys(results.data[0]);

    // Check if the expected columns are present in the file
    var isValidColumns1 = expectedColumns.every(function(column) {
        return columns.includes(column);
    });
    var isValidColumns2 = columns.every(function(column) {
        return expectedColumns.includes(column);
    });

    if (!isValidColumns1 || !isValidColumns2) {
        // Handle incorrect column headers
        handleFormError('Incorrect file format, download sample for more info!');
        uploadIconUnanimate();
        return;
    }

    if(results.data.length > 5000) {
        handleFormError('Uploading more than 5000 rows of data is not permitted!');
        uploadIconUnanimate();
        return false;
    }
    fileData = results.data;
    uploadData();
}

function handleUploadError(response) {
    var errors = JSON.parse(JSON.parse(response.responseText).message);

    if(errors.some((item) => item !== null)) {
        toastr.clear();
        toastr.error("Failed to upload the data, download errors for more info", 'Error', {
            timeOut: 36000,
            positionClass: 'toast-top-right',
            closeOnHover: false,
            closeButton: true,
        });
        populateErrorData(errors);
    }
}

function downloadErrors(fileName){
	writeFileData(errorData, 'tsv', fileName);
}

function toggleActiveNavLink() {
    var url = window.location.pathname.split("/");
    var pageId = '#' + url[url.length-1] + '-nav-link';
    $('.nav-link').removeClass('active');
    $(pageId).addClass('active');
}

function scientificNumberReviver(value) {
  if (typeof value === 'string' && /^[-+]?(\d+(\.\d*)?|\.\d+)(e[-+]?\d+)$/i.test(value)) {
    return parseFloat(value); // Parse as a float to preserve scientific notation
  }
  value = parseInt(value);
  return value;
}

function resetFormValidation($form) {
    $form.removeClass('was-validated');
    $form.find('.is-invalid').removeClass('is-invalid');
}

function  formatToDateAndTime(reqDate) {
    const months = [
       "Jan", "Feb", "Mar", "Apr", "May", "Jun",
       "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    ];

    // Assuming you have the date string "Wed Jul 12 2023 09:37:18 GMT+0530 (India Standard Time)"
//    const dateString = "Wed Jul 12 2023 09:37:18 GMT+0530 (India Standard Time)";

    // Create a Date object from the date string
    const date = new Date(reqDate);

    // Function to add leading zeros to single-digit numbers
    function addLeadingZero(number) {
      return number < 10 ? `0${number}` : number;
    }

    // Extract date components
    const day = addLeadingZero(date.getDate());
    const month = months[date.getMonth() + 1]; // Note: January is 0, so add 1 to get the correct month
    const year = date.getFullYear();
    const hours = addLeadingZero(date.getHours());
    const minutes = addLeadingZero(date.getMinutes());

    // Form the desired format
    const formattedDate = `${day} ${month} ${year} ${hours}:${minutes}`;

    return formattedDate;   // Output: "12 07 2023 09:37"
}

function formatToDate(date) {
  var months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
  var day = date.getDate();
  var month = months[date.getMonth()];
  var year = date.getFullYear();

  return month + " " + day + ", " + year;
}

function parseToDate(date) {
    var date = new Date(date * 1000);
}

$(document).ready(toggleActiveNavLink);
