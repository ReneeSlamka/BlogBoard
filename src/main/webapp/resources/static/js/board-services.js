
function createBoard() {
    var $newBoardname = encodeURI($("#new-board-name").val().trim());
    var apiUrl = window.location.href + "/boards";

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"boardName": $newBoardname},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            var newBoard = request.responseJSON;
            var error = newBoard.error;
            //Todo: temporary solution for user friendly error
            if(error === undefined) {
                addBoardName(newBoard.boardName, newBoard.boardUrl);
                $("#create-board-form").modal("toggle");
            }
            document.getElementById("new-board-name").value = "";
        }
    });
}

function saveBoardChanges(boardId, boardNameElementId) {
    var apiUrl = window.location.href + '/boards/' + boardId;
    var editedBoardName = document.getElementById("edited-board-name").value;

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"editedBoardName": editedBoardName},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            //Todo: temporary solution for user friendly error
            if(request.responseJSON.error === undefined) {
                document.getElementById(boardNameElementId).innerHTML = editedBoardName;
                $("#edit-board-form").modal("toggle");
            }
        }
    });
}

function deleteBoard(boardId, boardElementId) {
    var apiUrl = window.location.href + '/boards/' + boardId;

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
                $("#" + boardElementId).remove();
            }
        }
    });
}

function addMember() {
    var $memberUsername = $("#new-member-name").val().trim();
    var apiUrl = window.location.href + '/members';

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl,
        data: {"memberUsername": $memberUsername},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            var newMember = request.responseJSON;
            var error = newMember.error;
            //Todo: temporary solution for user friendly error
            if(error === undefined) {
                addMemberName(newMember.username, newMember.url);
                $("#add-member-board-form").modal("toggle");
            } else {
                $document.getElementById("new-member-name").value = "";
            }
        }
    });
}



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


function displayEditBoardModal(boardId, boardNameElementId) {
    document.getElementById("edit-board-form").style.display = "block";
    var button = document.getElementById("save-board-changes-button");
    button.onclick = function() {
        saveBoardChanges(boardId, boardNameElementId);
    }
}

function addBoardName(newBoardName, url) {
    var $listBoards = $("#owner-boards-list");
    $listBoards.append("<a href=" + url + "><h6>" + newBoardName+ "</h6></a>");
}

function addPostElement(title, textBody) {
    var newPost = "<div class=post> <header class= post-title> <h3>" + title + "</h3>" +
    "<h6>November 23rd, 2015</h6> <section class=post-button-panel>" +
        "<button class=post-button><i class=fa fa-1x fa-pencil-square-o></i></button>" +
    "<button class=post-button><i class=fa fa-trash-o></i></button> </section> </header> <p>"
    + textBody + "</p> <div></div> </div>";

    $("#posts-container").prepend(newPost);
}


function addMemberName(newMemberName, url) {
    var $listMembers = $("#list-board-members");
    //$listMembers.append("<a href=" + url + "><h5>" + newMemberName + "</h5></a>");
    var newMemberElement = "<div class=board-member>" +
            "<img class=user-avatar src=" + "https://www.tubestart.com/upload/thumb/user_4823_square.jpeg/>" +
            "<h5 class=board-info>" + newMemberName + "</h5>" +
    "</div>";
    $listMembers.append(newMemberElement);

}
//function to parse board cookie from server to list names on home page
function parseJSONBoardCookies(jsonArray) {
    //remove extra quotation marks
    jsonArray = jsonArray.substring(1, boardName.length-1);
    jsonArray = jsonArray.split(/[\[\]']+/g);
    jsonArray = jsonArray[1].replace(/\\/g, "");

    var arrayCookieJson = jsonArray.split(",");

    for (var i = 0; i < arrayCookieJson.length; i=i+2) {
        arrayCookieJson[i/2] = arrayCookieJson[i] + "," + arrayCookieJson[i+1];
    }
}