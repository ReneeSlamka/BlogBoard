package com.blogboard.server.web;

import com.blogboard.server.service.BoardService;
import com.blogboard.server.data.entity.Board;


public class BoardServiceResponse extends Response {

    private static final String BOARD_CREATION_SUCCESS = "Your board has been successfully created!";
    private  String BOARD_CREATION_FAILURE = "Sorry, it seems there is already a board with that name. Please try making" +
            " another board with a different name.";

    private static final String GET_BOARD_SUCCESS = "Board successfully retrieved";
    private static final String GET_BOARD_FAILURE_NAME = "Error, board with that name doesn't exist";
    private static final String GET_BOARD_FAILURE_USERNAME = "Error, user does not have permission to access this board";

    private static final String ADD_MEMBER_SUCCESS = "New member successfully added";
    private static final String ADD_MEMBER_FAILURE = "Failed to add new member, account with given username doesn't exist";

    private static final String REMOVE_MEMBER_SUCCESS = "Member successfully removed";
    private static final String REMOVE_MEMBER_FAILURE = "Failed to remove member, account with given username doesn't exist";

    private static final String POST_SUCCESSFULLY_ADDED = "Post successfully added";
    private static final String POST_SUCCESSFULLY_DELETED = "Post successfully deleted";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";

    private BoardService.Service serviceType;

    private Board board;

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

    @Override
    public void setToSuccess() {
        switch(serviceType) {

            case BOARD_CREATION:
                this.setResponseMessage(BOARD_CREATION_SUCCESS);
                break;

            case GET_BOARD:
                this.setResponseMessage(GET_BOARD_SUCCESS);
                break;

            case ADD_MEMBER:
                this.setResponseMessage(ADD_MEMBER_SUCCESS);
                break;

            case REMOVE_MEMBER:
                this.setResponseMessage(REMOVE_MEMBER_SUCCESS);
                break;

            case ADD_POST:
                this.setResponseMessage(POST_SUCCESSFULLY_ADDED);
                break;

            case DELETE_POST:
                this.setResponseMessage(POST_SUCCESSFULLY_DELETED);
                break;

            default:
                this.setResponseMessage("Undefined success");
                break;
        }
    }


    public void setToFailure(BoardService.CauseOfFailure causeOfFailure) {
        this.setResponseMessage(UNKNOWN_ERROR);
        switch(serviceType) {

            case BOARD_CREATION:
                if (causeOfFailure.equals(BoardService.CauseOfFailure.NAME)) {
                    this.setResponseMessage(BOARD_CREATION_FAILURE);
                }
                break;

            case GET_BOARD:
                if (causeOfFailure.equals(BoardService.CauseOfFailure.NAME)) {
                    this.setResponseMessage(GET_BOARD_FAILURE_NAME);
                } else if (causeOfFailure.equals(BoardService.CauseOfFailure.USERNAME)) {
                    this.setResponseMessage(GET_BOARD_FAILURE_USERNAME);
                }
                break;

            case ADD_MEMBER:
                if (causeOfFailure.equals(BoardService.CauseOfFailure.USERNAME)) {
                    this.setResponseMessage(ADD_MEMBER_FAILURE);
                }
                break;

            case REMOVE_MEMBER:
                if (causeOfFailure.equals(BoardService.CauseOfFailure.USERNAME)) {
                    this.setResponseMessage(REMOVE_MEMBER_FAILURE);
                }
                break;
        }

    }
}
