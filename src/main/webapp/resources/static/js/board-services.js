
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
                addBoardName(boardName);
            }
            document.getElementById("new-board-name").value = "";
            if (window.location.href !== url) {
                window.location.href = url;
            }

        }
    });
}

function addBoardName(newBoardName) {
    var $listBoards = $("#user-boards-list");
    $listBoards.append("<li>" + newBoardName+ "</li>");
}

