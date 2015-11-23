package com.blogboard.server.web;

import com.blogboard.server.service.BoardService;
import com.blogboard.server.data.entity.Board;
import org.json.simple.JSONObject;


public class BoardServiceResponse extends Response {

    private static final String BOARD_CREATION_SUCCESS = "Your board has been successfully created!";
    private static final String GET_BOARD_SUCCESS = "Board successfully retrieved";

    private static final String ADD_MEMBER_SUCCESS = "New member successfully added";


    private static final String REMOVE_MEMBER_SUCCESS = "Member successfully removed";

    private static final String POST_SUCCESSFULLY_ADDED = "Post successfully added";
    private static final String POST_SUCCESSFULLY_DELETED = "Post successfully deleted";


    private BoardService.Service serviceType;

    private Board board;

    private JSONObject jsonReponseObject;

    public BoardServiceResponse() {};

    public BoardServiceResponse(BoardService.Service serviceType) {
        super();
        this.serviceType = serviceType;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setJsonReponseObject(JSONObject jsonObject) { this.jsonReponseObject = jsonObject; }

    public JSONObject getJsonReponseObject() { return jsonReponseObject; }

    @Override
    public void setToSuccess() {
        switch(serviceType) {

            case BOARD_CREATION:
                this.setMessage(BOARD_CREATION_SUCCESS);
                break;

            case GET_BOARD:
                this.setMessage(GET_BOARD_SUCCESS);
                break;

            case ADD_MEMBER:
                this.setMessage(ADD_MEMBER_SUCCESS);
                break;

            case REMOVE_MEMBER:
                this.setMessage(REMOVE_MEMBER_SUCCESS);
                break;

            case ADD_POST:
                this.setMessage(POST_SUCCESSFULLY_ADDED);
                break;

            case DELETE_POST:
                this.setMessage(POST_SUCCESSFULLY_DELETED);
                break;

            default:
                this.setMessage("Undefined success");
                break;
        }
    }
}
