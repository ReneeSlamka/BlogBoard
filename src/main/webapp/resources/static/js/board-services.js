
function createBoard() {
    var $newBoardname = $("#new-board-name").val();

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
                var boardName = getCookie("userBoards");
                //remove extra quotation marks
                boardName = boardName.substring(1, boardName.length-1);
                boardName = boardName.split(/[\[\]']+/g);
                boardName = boardName[1].replace(/\\/g, "");
                
                var arrayCookieJson = boardName.split(",");

                for (var i = 0; i < arrayCookieJson.length; i=i+2) {
                    arrayCookieJson[i/2] = arrayCookieJson[i] + "," + arrayCookieJson[i+1];
                    var cookieJSON = JSON.parse(arrayCookieJson[i/2]);
                    addBoardName(cookieJSON.name, cookieJSON.url);
                }
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