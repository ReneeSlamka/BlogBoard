package com.blogboard.server.service;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.web.BoardServiceResponse;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class BoardService {

    private static final String BASE_URL = "http://localhost:8080";
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();

            Board newBoard = new Board(name, ownerUsername, dateFormat.format(calendar.getTime()), BASE_URL);
            Board savedBoard = boardRepo.save(newBoard);

            //configure response
            //store list of ALL users boards with urls into cookie
            ArrayList<Board> boards = boardRepo.findByOwnerUsername(ownerUsername);
            JSONArray boardCookies = new JSONArray();
            for (Board board: boards) {
                JSONObject boardCookieTuple = new JSONObject();
                boardCookieTuple.put("name", board.getName());
                boardCookieTuple.put("url", board.getUrl());
                boardCookies.add(boardCookieTuple);
            }

            //TODO: will have to also get boards that are a member but not owner of (later)

            Cookie userBoardsCookie = new Cookie("userBoards", boardCookies.toJSONString());
            userBoardsCookie.setMaxAge(60*15);
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
