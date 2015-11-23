package com.blogboard.server.service;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.web.BoardServiceResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public enum Service {
        BOARD_CREATION, GET_BOARD, ADD_MEMBER, REMOVE_MEMBER, ADD_POST, DELETE_POST
    }

    //Custom Success/Error Messages

    private static final String BOARD_CREATED = "Your board has been successfully created!";
    private static final String BOARD_FOUND = "Board successfully retrieved";
    private static final String MEMBER_ADDED = "New member successfully added";
    private static final String MEMBER_REMOVED = "Member successfully removed";
    private static final String POST_ADDED = "Post successfully added";
    private static final String POST_DELETED = "Post successfully deleted";

    private static final String NAME_IN_USE = "Sorry, it seems there is already a board with that name";
    private static final String BOARD_NOT_FOUND = "Error, board with that name doesn't exist";
    private static final String BOARD_ACCESS_DENIED = "Error, you do not have permission to access this board";
    private static final String ADD_MEMBER_FAILURE = "Failed to add new member, account with given username doesn't exist";
    private static final String REMOVE_MEMBER_FAILURE = "Failed to remove member, account with given username " +
                                                        "either doesn't exist or isn't a member of this board";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";


    /*
    * Method Name: Create Board
    * Inputs: Board Repository, name (of board), ownerUsername
    * Return Value: BoardServiceResponse containing newly created board object and  configured httpServerletResponse
    * Purpose: create new board, store in database and return the necessary info to add it to
    * the DOM on the client side
    */
    public BoardServiceResponse createBoard(BoardRepository boardRepo, String name, String ownerUsername,
                                            HttpServletResponse httpResponse) throws IOException {

        BoardServiceResponse createBoardResponse = new BoardServiceResponse(Service.BOARD_CREATION);

        //check if board with that owner AND name already exists
        if (boardRepo.findByNameAndOwnerUsername(name, ownerUsername) == null) {

            //create board and save in board repo
            Board newBoard = new Board(name, ownerUsername, AppServiceHelper.createTimeStamp(), BASE_URL);
            createBoardResponse.setBoard(newBoard);
            Board savedBoard = boardRepo.save(newBoard);

            //update list of boards to store in cookie on client side (use this later)
            ArrayList<Board> boards = boardRepo.findByOwnerUsername(ownerUsername);
            JSONArray boardCookies = new JSONArray();
            for (Board board: boards) {
                JSONObject boardCookieTuple = new JSONObject();
                boardCookieTuple.put("name", board.getName());
                boardCookieTuple.put("url", board.getUrl());
                boardCookies.add(boardCookieTuple);
            }

            //TODO: will have to also get boards that are a member but not owner of (later)

            //configure cookie and api response
            Cookie userBoardsCookie = new Cookie("userBoards", boardCookies.toJSONString());
            AppServiceHelper.configureCookie(userBoardsCookie, 60*15, "/", false, false);
            httpResponse.addCookie(userBoardsCookie);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            createBoardResponse.setMessage(BOARD_CREATED);

        } else {
            //return error messages saying board already exists
            httpResponse.sendError(HttpServletResponse.SC_OK, NAME_IN_USE);
        }

        //include URL for client to redirect to
        httpResponse.setHeader("Location", USER_HOME_URL);
        return createBoardResponse;
    }


    /*
    * Method Name: Get List Boards
    * Inputs: Board Repository, username(board ownerUsername)
    * Return: ArrayList<Board>
    * Purpose: to retrieve all boards that belong to that user
    */
    public ArrayList<Board> getListBoards(BoardRepository boardRepo, String username) {
        ArrayList<Board> userBoards =  boardRepo.findByOwnerUsername(username);
        return userBoards;
    }


    /*
    * Method Name: Get Board
    * Inputs: Board Repository, name (of board), ownerUsername
    * Return: ArrayList<Board>
    * Purpose:
    */
    public BoardServiceResponse getBoard(BoardRepository boardRepo, String name, String username,
                                         HttpServletResponse httpResponse) throws IOException {

        BoardServiceResponse getBoardResponse = new BoardServiceResponse(Service.GET_BOARD);
        Board targetBoard = boardRepo.findByName(name);

        //check that board exists and is accessible by user
        //Todo: check if username is in list of members
        if (targetBoard != null && (targetBoard.getOwnerUsername().equals(username))) {
            getBoardResponse.setBoard(targetBoard);
            getBoardResponse.setMessage(BOARD_FOUND);
        } else if (!targetBoard.getOwnerUsername().equals(username)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, BOARD_ACCESS_DENIED);
        } else if (targetBoard == null) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
        }

        return getBoardResponse;
    }


    /*
    * Method Name: Add Member
    * Inputs:
    * Return:
    * Purpose:
    */
    public BoardServiceResponse addMember(AccountRepository accountRepo, BoardRepository boardRepo,
        String username, HttpServletResponse httpResponse, String boardName) {
        BoardServiceResponse addMemberResponse = new BoardServiceResponse(Service.ADD_MEMBER);

        //1. Check if user exists
        /*if (accountRepo.findByUsername(username) != null) {
            Board targetBoard = boardRepo.findByName(boardName);
            targetBoard.addMember(username);
            JS
        } else {

        }*/

        //2. When confirmed, add user's name to list of boards members

        //3. Create json object with user's name, and link to their home page

        //4. Configure httpRespnse and BoardServiceResponse

        //5. Send response to client so can add user to DOM

        return addMemberResponse;
    }
}
