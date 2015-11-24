
function createAccount() {
    var $newUsername = $("#new-username").val();
    var $newPassword = $("#new-password").val();
    var $newEmail = $("#new-email").val();

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/accounts',
        data: {"username": $newUsername, "password": $newPassword, "email": $newEmail},
        dataType: 'json',
        crossDomain: true,
        complete: function (request, textStatus) { //for additional info
            showResponseMessage("create-account-response-text", request.responseJSON.message);
            document.getElementById("new-username").value = "";
            document.getElementById("new-password").value = "";
            document.getElementById("new-email").value = "";
        }
    });
}

function showResponseMessage(elementId, text) {
    var display = document.getElementById(elementId);
    display.innerHTML = text;
    display.style.display="block";
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
