
function createAccount() {
    var $newUsername = $("#new-username").val().trim();
    var $newPassword = $("#new-password").val().trim();
    var $newEmail = $("#new-email").val().trim();

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/accounts',
        data: {"username": $newUsername, "password": $newPassword, "email": $newEmail},
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) { //for additional info
            showResponseMessage("create-account-response-text", request.responseJSON.message, textStatus);
            document.getElementById("new-username").value = "";
            document.getElementById("new-password").value = "";
            document.getElementById("new-email").value = "";
        }
    });
}

function showResponseMessage(elementId, text, textStatus) {
    var textDisplay = document.getElementById(elementId);
    textDisplay.innerHTML = text;
    textDisplay.style.background = "#77F45A";
    var fadeOutTime = 3000;
    if (textStatus ==="error") {
        textDisplay.style.background = "#ffc1c1";
        fadeOutTime = 5000;
    }
    textDisplay.style.display="block";
    $("#" + elementId).fadeOut(fadeOutTime);
}

function login(event) {
    var $name = $("#login-username").val();
    var $password = $("#login-password").val();

    $.ajax({
        type: 'GET',
        cache: false,
        async: false,
        url: 'http://localhost:8080/accounts',
        data: {"username": $name, "password": $password},
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) { //for additional info
            alert(request.responseText);
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            window.location.href = url;
        }
    });
}

function changeEmail() {
    var $newEmailAddress = $("#new-email-address").val().trim();
    var apiUrl = window.location.href + "/account/email";

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"newEmailAddress": $newEmailAddress},
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) {
            alert(request.responseText);
            if(request.error === undefined) {
                document.getElementById("user-email-address").innerHTML = $newEmailAddress;
               $("#change-email-form").modal("toggle");
            }
        }
    });
}

function changePassword() {
    var $oldPassword = $("#old-password").val().trim();
    var $newPassword = $("#new-password").val().trim();
    var apiUrl = window.location.href + "/account/password";

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"oldPassword": $oldPassword, "newPassword" : $newPassword},
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) {
            alert(request.responseText);
            if(request.error === undefined) {
                $("#change-password-form").modal("toggle");
            }
        }
    });
}

function logout(event) {
    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/sessions',
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) { //for additional info
            alert(request.responseText);
            if (request.responseJSON.sessionId != null) {
                createSessionCookie(request.responseJSON.sessionId, request.responseJSON.sessionUsername, 60);
            }
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            if (url !== null) {
                window.location.href = url;
            }
        }
    });
}

function createSessionCookie(sessionId, username, maxAge) {
    var sessionInfo = {
        sessionId: sessionId,
        username: username
    };
    var sessionInfoJSON = JSON.stringify(sessionInfo);
    document.cookie = "sessionIdCookie=" + sessionInfoJSON + ";max-age=" + maxAge;
}


function getCookieField(fieldName) {
    var field = fieldName + "=";
    var cookieFields = document.cookie.split(';');

    for (var i = 0; i < cookieFields.length; i++) {
        var c = cookieFields[i];
        //TODO alter check for empty cookies?
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(field) == 0) {
            return c.substring(field.length, c.length);
        }
    }
    return "";
}
