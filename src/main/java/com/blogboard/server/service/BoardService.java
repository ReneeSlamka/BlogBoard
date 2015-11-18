package com.blogboard.server.service;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.web.BoardServiceResponse;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class BoardService {

    private static final String USER_HOME_URL = "http://localhost:8080/home";

    public enum CauseOfFailure {
        NAME, USERNAME, UNKNOWN
    }
    public enum Service {
        BOARD_CREATION, GET_BOARD, ADD_MEMBER, REMOVE_MEMBER, ADD_POST, DELETE_POST
    }

    public BoardServiceResponse createBoard(BoardRepository boardRepo, String name, String ownerUsername,
        HttpServletResponse httpResponse) {

        BoardServiceResponse createBoardResponse = new BoardServiceResponse(Service.BOARD_CREATION);

        //check if board with that owner AND name already exists
        if (boardRepo.findByNameAndOwnerUsername(name, ownerUsername) == null) {
            //create board and save in board repo
            Board newBoard = new Board(name, ownerUsername);
            Board savedBoard = boardRepo.save(newBoard);

            //configure response
            Cookie userBoardsCookie = new Cookie("userBoards", name);
            userBoardsCookie.setMaxAge(60*10);
            userBoardsCookie.setPath("/");
            httpResponse.addCookie(userBoardsCookie);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            createBoardResponse.setToSuccess();

        } else {
            //return error messages saying board already exists
            createBoardResponse.setToFailure(CauseOfFailure.NAME);
            httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        httpResponse.setHeader("Location", USER_HOME_URL);
        return createBoardResponse;
    }

    public BoardServiceResponse getBoard(BoardRepository boardRepo, String name, String username) {
        BoardServiceResponse getBoardResponse = new BoardServiceResponse(Service.GET_BOARD);
        Board targetBoard = boardRepo.findByName(name);

        //check that board exists and is accsesible by user
        //Todo: check if username is in list of members
        if (targetBoard != null && (targetBoard.getOwnerUsername().equals(username))) {
            getBoardResponse.setBoard(targetBoard);
            getBoardResponse.setToSuccess();
        } else if (!targetBoard.getOwnerUsername().equals(username)) {
            getBoardResponse.setToFailure(CauseOfFailure.USERNAME);
        } else if (targetBoard == null) {
            getBoardResponse.setToFailure(CauseOfFailure.NAME);
        } else {
            getBoardResponse.setToFailure(CauseOfFailure.UNKNOWN);
        }

        return getBoardResponse;
    }
}
