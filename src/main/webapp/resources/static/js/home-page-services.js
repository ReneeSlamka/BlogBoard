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

function displayModal(modalElementId) {
    var modal = document.getElementById(modalElementId);
    modal.style.display = "block";
}