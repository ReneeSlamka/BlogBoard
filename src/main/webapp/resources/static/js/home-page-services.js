function setUsername() {
    var sessionUsername = getCookie("sessionUsername");

    //potentially change second case later
    if (sessionUsername !== null || sessionUsername.length > 0) {
        document.getElementById("home-username").innerHTML = sessionUsername;
    } else {
        document.getElementById("home-username").innerHTML = "DEFAULT";
    }
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


setUsername();