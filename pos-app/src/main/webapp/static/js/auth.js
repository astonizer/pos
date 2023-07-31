function getBaseUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl
}

function getLoginUrl(){
	 return getBaseUrl() + "/api/session/login";
}

function getSignUpUrl(){
	 return getBaseUrl() + "/api/session/signup";
}

function loginUser(event) {
    event.preventDefault(); // Prevent form submission

    var $form = $('#login-form');
    if (!$form[0].checkValidity()) {
        event.preventDefault(); // Prevent form submission
        event.stopPropagation(); // Stop event propagation
        $form.addClass('was-validated');
        handleFormError("Please enter valid credentials");
        return false;
    } else {
        $form.addClass('was-validated');
    }

    loginAnimation();

    // Serialize form data into JSON
    var formData = {
        email: $('input[name="email"]').val(),
        password: $('input[name="password"]').val()
    };
    var jsonData = JSON.stringify(formData);

    // Send AJAX request to the backend
    $.ajax({
        url: getLoginUrl(),
        type: 'POST',
        contentType: 'application/json',
        data: jsonData,
        success: function(response) {
            disableLoginAnimation();
            window.location.href = getBaseUrl() + '/ui/home';
        },
        error: function(error) {
            disableLoginAnimation();
            resetForm();
            handleAjaxError(error);
        }
    });
}

function signupUser(event) {
    event.preventDefault(); // Prevent form submission

    if($('#password').val() !== $('#confirm-password').val()) {
        $('#password').val('')
        $('#confirm-password').val('')
        handleFormError('Password and Confirm Password do not match. Please make sure they are the same.');
        return;
    }

    signupAnimation();

    // Serialize form data into JSON
    var formData = {
        email: $('input[name="email"]').val(),
        password: $('input[name="password"]').val()
    };
    var jsonData = JSON.stringify(formData);

    // Send AJAX request to the backend
    $.ajax({
        url: getSignUpUrl(),
        type: 'POST',
        contentType: 'application/json',
        data: jsonData,
        success: function(response) {
            disableSignupAnimation
            window.location.href = getBaseUrl() + '/ui/home';
        },
        error: function(error) {
            disableSignupAnimation();
            resetForm();
            handleAjaxError(error);
        }
    });
}

function resetForm() {
    $('#email').val('');
    $('#password').val('');
    $('#confirm-password').val('');
}

function loginAnimation() {
    $('#login-button').css('background-color', 'grey');
    $('#login-button').val('Signing in...');
    $('#login-button').prop('disabled', true);
}
function disableLoginAnimation() {
    $('#login-button').css('background-color', 'var(--color-blue)');
    $('#login-button').val('Sign in');
    $('#login-button').prop('disabled', false);
}

function signupAnimation() {
    $('#signup-button').css('background-color', 'grey');
    $('#signup-button').val('Signing up...');
    $('#signup-button').prop('disabled', true);
}
function disableSignupAnimation() {
    $('#signup-button').css('background-color', 'var(--color-blue)');
    $('#signup-button').val('Sign up');
    $('#signup-button').prop('disabled', false);
}

$(document).ready(function() {
    $('#login-form').submit(loginUser);
    $('#signup-form').submit(signupUser);

    $('#login-button').prop('disabled', false);
});
