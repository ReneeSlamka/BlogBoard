/**
 * Created by renee on 2015-11-10.
 */

//var accountServices = function() {

function createAccount() {
    var $newUsername = $("#new-username").val();
    var $newPassword = $("#new-password").val();
    var $newEmail = $("#new-email").val();

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/account',
        data: {"username": $newUsername, "password": $newPassword, "email": $newEmail},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) { //for additional info
            alert(request.responseText);
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            window.location.href = url;

        }
    });
}

function login(event) {
    var $name = $("#login-username").val();
    var $password = $("#login-password").val();

    $.ajax({
        type: 'GET',
        cache: false,
        async: false,
        url: 'http://localhost:8080/account',
        data: {"username": $name, "password": $password},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) { //for additional info
            alert(request.responseText);
            if (request.responseJSON.sessionId != null) {
                createSessionCookie(request.responseJSON.sessionId, request.responseJSON.sessionUsername, 60);
            }
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            window.location.href = url;

        }
    });
}
//};

function createSessionCookie(sessionId, username, maxAge) {
    var sessionInfo = {
        sessionId: sessionId,
        username: username
    };

    var sessionInfoJSON = JSON.stringify(sessionInfo);
    document.cookie = "sessionIdCookie=" + sessionInfoJSON + ";max-age=" + maxAge;
}


function getCookieField(field) {
    var field = field + "=";
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
