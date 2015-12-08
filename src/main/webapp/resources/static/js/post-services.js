
function addPost() {
    var apiUrl = window.location.href + '/posts';
    var $title = $("#new-post-title").val().trim();
    var $textBody = $("#new-post-text").val().trim();

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"title": $title, "textBody": $textBody},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            //Todo: temporary solution for user friendly error
            if(request.responseJSON.error === undefined) {
                addPostElement($title, $textBody);
                document.getElementById("#new-post-title").value = "";
                document.getElementById("#new-post-text").value = "";
            }
        }
    });
}


function savePostChanges(postId, titleId, textContentId) {
    var apiUrl = window.location.href + '/posts/' + postId;
    var title = document.getElementById("edit-" + titleId).value;
    var textBody = document.getElementById("edit-" + textContentId).value;

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"title": title, "textBody": textBody},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            //Todo: temporary solution for user friendly error
            if(request.responseJSON.error === undefined) {
                document.getElementById("edit-" + titleId).style.display = "none";
                document.getElementById("edit-" + textContentId).style.display = "none";
                document.getElementById(titleId).style.display = "block";
                document.getElementById(titleId).innerHTML = title;
                document.getElementById(textContentId).style.display = "block";
                document.getElementById(textContentId).innerHTML = textBody;
            }
        }
    });
}


function showEditingFields(titleId, textContentId) {
    var title = document.getElementById(titleId).innerHTML;
    var textBody =  document.getElementById(textContentId).innerHTML;

    var inputTitleElement =  document.getElementById("edit-" + titleId);
    inputTitleElement.style.display = "block";
    inputTitleElement.value = title;

    var inputTextContent = document.getElementById("edit-" + textContentId);
    inputTextContent.style.display = "block";
    inputTextContent.innerHTML = textBody;

    document.getElementById(titleId).style.display = "none";
    document.getElementById(textContentId).style.display = "none";
}


function deletePost(postId, postElementId) {
    var apiUrl = window.location.href + '/posts/' + postId;

    $.ajax({
        type: 'DELETE',
        cache: false,
        async: false,
        url: apiUrl,
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            //Todo: temporary solution for user friendly error
            if(request.responseJSON.error === undefined) {
                $("#" + postElementId).remove();
            }
        }
    });
}