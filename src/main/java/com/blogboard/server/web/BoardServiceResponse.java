package com.blogboard.server.web;

import com.blogboard.server.service.BoardService;
import com.blogboard.server.data.entity.Board;
import org.json.simple.JSONObject;


public class BoardServiceResponse extends BasicAPIResponse {

    private Board board;

    private JSONObject jsonReponseObject;

    public BoardServiceResponse() {};

    public BoardServiceResponse(BoardService.Service serviceType) {
        super();
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setJsonReponseObject(JSONObject jsonObject) { this.jsonReponseObject = jsonObject; }

    public JSONObject getJsonReponseObject() { return jsonReponseObject; }
}
