
function createBoard() {
    var test = $("#new-board-name").val();
    var $newBoardname = encodeURI($("#new-board-name").val());

    $.ajax({
        type: 'POST',
        cache: false,
        async: false,
        url: 'http://localhost:8080/board',
        data: {"boardName": $newBoardname},
        dataType: 'json',
        crossDomain: true,
        error: function (request, textStatus, errorThrown) {
            alert(textStatus);
        },
        complete: function (request, textStatus) { //for additional info
            alert(request.responseText);
            var headers = request.getAllResponseHeaders();
            var url = request.getResponseHeader("Location");
            if(textStatus === "success") {
                $("#create-board-form").modal("hide");
                //var boardName = getCookie("userBoards");
                //parseJSONBoardCookies(boardName)
                var newBoard = request.responseJSON.board;
                addBoardName(newBoard.name, newBoard.url);
            }
            document.getElementById("new-board-name").value = "";
            if (window.location.href !== url) {
                window.location.href = url;
            }
        }
    });
}

function addBoardName(newBoardName, url) {
    var $listBoards = $("#user-boards-list");
    $listBoards.append("<a href=" + url + "><li>" + newBoardName+ "</li></a>");
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