
function validateSession() {

    var sessionCookie = getCookie("sessionIdCookie");
    alert("sessionCookie value:" + sessionCookie);
    var serverCookie = getCookie("sessionID");
    alert("serverCookie value:" + serverCookie);

    var sessionJSONInfo;
    if (sessionCookie !== null && sessionCookie.length > 0) {
        sessionJSONInfo = JSON.parse(sessionCookie);
        alert(sessionJSONInfo);
    } else {
        sessionJSONInfo = {
            sessionId: "",
            username: ""
        }

    }

    $.ajax({
        cache: false,
        async: false,
        type: 'GET',
        url: "http://localhost:8080/session",
        data: sessionJSONInfo,
        dataType: 'json',
        crossDomain: true,
        xhrFields: {
            withCredentials: true
        },
        success: function(result){
            alert("token recieved: " + result);
            //set updated sessionId from cookie here
        },
        error: function(request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function(request, textStatus) { //for additional info
            alert(request.responseText);
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            alert("url is:" + url);
            if (window.location.href !== url) {
                window.location.href = url;
            }
        }
    });
}

function getCookie(field) {
    var field = field + "=";
    var cookieFields = document.cookie.split(';');

    for(var i=0; i < cookieFields.length; i++) {
        var c = cookieFields[i];
        //TODO alter check for empty cookies?
        while (c.charAt(0)==' ') {
            c = c.substring(1);
        }
        if (c.indexOf(field) == 0) {
            return c.substring(field.length, c.length);
        }
    }
    return "";
}

validateSession();