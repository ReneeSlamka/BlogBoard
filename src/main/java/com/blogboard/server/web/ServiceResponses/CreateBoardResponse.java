package com.blogboard.server.web.ServiceResponses;

import com.blogboard.server.web.BasicResponse;


public class CreateBoardResponse extends BasicResponse {

    private String boardName;
    private String boardUrl;

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardUrl(String boardUrl) {
        this.boardUrl = boardUrl;
    }

    public String getBoardUrl() {
        return boardUrl;
    }
}
