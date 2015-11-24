
function createBoard() {
    var $newBoardname = encodeURI($("#new-board-name").val());

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/boards',
        data: {"boardName": $newBoardname},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            if(textStatus === "success") {
                $("#create-board-form").modal("hide");
                //var boardName = getCookie("userBoards");
                //parseJSONBoardCookies(boardName)
                var newBoard = request.responseJSON.board;
                addBoardName(newBoard.name, newBoard.url);
            }
            document.getElementById("new-board-name").value = "";
        }
    });
}

function addMember() {
    var $memberUsername = $("#member-username").val();
    var apiUrl = window.location.href;

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: apiUrl + '/members',
        data: {"memberUsername": $memberUsername},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) {
            alert(request.responseText);
            var newMember = request.responseJSON;
            if(textStatus === "success") {
                addMemberName(newMember.username, newMember.url);
            }
        }
    });
}

function addBoardName(newBoardName, url) {
    var $listBoards = $("#user-boards-list");
    $listBoards.append("<a href=" + url + "><li>" + newBoardName+ "</li></a>");
}


function addMemberName(newMemberName, url) {
    var $listMembers = $("#list-board-members");
    $listMembers.append("<a href=" + url + "><h5>" + newMemberName + "</h5></a>");

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
        //var cookieJSON = JSON.parse(arrayCookieJson[i/2]);
        //addBoardName(cookieJSON.name, cookieJSON.url);
    }
}