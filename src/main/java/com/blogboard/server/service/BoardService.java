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
        NAME, USERNAME
    }
    public enum Service {
        BOARD_CREATION, ADD_MEMBER, REMOVE_MEMBER, ADD_POST, DELETE_POST
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
}
