
function savePostChanges(postId, postTitleInputId, postTextContentInputId) {
    var apiUrl = window.location.href + '/posts/' + postId;
    var $title = $("#" + postTitleInputId).val().trim();
    var $textBody = $("#" + postTextContentInputId).val().trim();

    $.ajax({
        type: 'PUT',
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

            }
        }
    });
}

function showEditingFields(titleEditingInputId, textContentEditingInputId) {
    document.getElementById(titleEditingInputId).style.display = "block";
    document.getElementById(textContentEditingInputId).style.display = "block";
}

function deletePost(postId, postElementId) {
    var apiUrl = window.location.href + '/posts/' + postId;

    $.ajax({
        type: 'POST',
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