package com.blogboard.server.service;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.web.ServiceResponses.AddMemberResponse;
import com.blogboard.server.web.ServiceResponses.CreateBoardResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import com.blogboard.server.data.entity.Account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

@Service
public class BoardService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String BASE_BOARD_URL = BASE_URL + File.separator + "boards";

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
    private static final String USER_NOT_FOUND = "Failed to add new member, account with given username doesn't exist";
    private static final String USER_ALREADY_MEMBER = "This user is already a member of this board";
    private static final String REMOVE_MEMBER_FAILURE = "Failed to remove member, account with given username " +
                                                        "either doesn't exist or isn't a member of this board";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";


    /*
    * Method Name: Create Board
    * Inputs: Board Repository, name (of board), ownerUsername
    * Return Value: CreateBoardResponse containing newly created board object and httpResponse
    * Purpose: create new board, store in database and return the necessary info to add it to
    * the DOM on the client side
    */
    public CreateBoardResponse createBoard(BoardRepository boardRepo, AccountRepository accountRepo, String boardName,
                                           String ownerUsername, HttpServletResponse httpResponse) throws IOException {

        CreateBoardResponse response = new CreateBoardResponse();
        String decodedName = AppServiceHelper.decodeString(boardName);
        Account boardOwner = accountRepo.findByUsername(ownerUsername);

        if (boardOwner == null) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, USER_NOT_FOUND);
            return response;
        }

        //SUCCESS CASE: board with that name does not already exist (for this user)
        if (boardRepo.findByNameAndOwner(decodedName, boardOwner) == null) {
            //create board and save in board repo
            Board newBoard = new Board(decodedName, boardOwner, AppServiceHelper.createTimeStamp());
            Board savedBoard = boardRepo.save(newBoard);
            savedBoard.setUrl(BASE_BOARD_URL);
            savedBoard = boardRepo.save(savedBoard);
            response.setBoardName(savedBoard.getName());
            response.setBoardUrl(savedBoard.getUrl());

            httpResponse.setStatus(HttpServletResponse.SC_CREATED);
            response.setMessage(BOARD_CREATED);
        } else {
            //FAILURE CASE: board with given name already exists (for this user)
            httpResponse.sendError(HttpServletResponse.SC_OK, NAME_IN_USE);
        }

        httpResponse.setHeader("Location", BASE_URL + File.separator + ownerUsername);
        return response;
    }


    /*
    * Method Name: Get Board
    * Inputs: Board Repository, name (of board), ownerUsername
    * Return: ArrayList<Board>
    * Purpose: return a board so that its information can be rendered in its page
    */
    public Board getBoard(BoardRepository boardRepo, AccountRepository accountRepo, Long boardId, String username,
                                         HttpServletResponse httpResponse) throws IOException {

        Board targetBoard = boardRepo.findOne(boardId);
        Account targetMember = accountRepo.findByUsername(username);

        if (targetMember == null) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, USER_NOT_FOUND);
            return null;
        }

        //check that board exists and is accessible by user
        //Todo: check if username is in list of members
        if (targetBoard != null && (targetBoard.getMembers().contains(targetMember))) {
           httpResponse.setStatus(HttpServletResponse.SC_OK);
        } else if (targetBoard == null) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
        } else if (!targetBoard.getMembers().contains(targetMember)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, BOARD_ACCESS_DENIED);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
        }

        return targetBoard;
    }


    /*
   * Method Name: Get List Boards
   * Inputs: Board Repository, username(board ownerUsername)
   * Return: ArrayList<Board>
   * Purpose: to retrieve all boards that either belong to that user or that user is a member of
   */
    public ModelAndView getHomePageBoardsList(BoardRepository boardRepo, AccountRepository accountRepo,
                                              String username, HttpServletResponse httpResponse) throws IOException {

        ModelAndView mav = new ModelAndView();

        Account user = accountRepo.findByUsername(username);
        if (user == null) {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, USER_NOT_FOUND);
            return mav;
        }

        ArrayList<Account> memberSearchList = new ArrayList<Account>();
        memberSearchList.add(user);
        ArrayList<Board> createdBoards = boardRepo.findByOwner(user);
        ArrayList<Board> memberBoards = boardRepo.findByMembersIn(memberSearchList);

        for (Board board : memberBoards) {
            if (board.getOwner().equals(user)) {
                memberBoards.remove(board);
            }
        }

        mav.addObject("createdBoards", createdBoards);
        mav.addObject("memberBoards", memberBoards);
        mav.setViewName("home");
        httpResponse.setStatus(HttpServletResponse.SC_OK);

        return mav;
    }


    /*
    * Method Name: Add Member
    * Inputs: Account Repository, Board Repository, (sessionUsername & sessionID later), memberUsername,
    * HTTPServlet Response
    * Return: Object containing the username of the new member, its url (add later), and http response
    * Purpose: To add the username of the new member to a board's members list
    */
    public AddMemberResponse addMember(AccountRepository accountRepo, BoardRepository boardRepo,
        String username, Long boardId, HttpServletResponse httpResponse) throws IOException {

        AddMemberResponse response = new AddMemberResponse();
        Account targetAccount = accountRepo.findByUsername(username);

        if (targetAccount != null) {
            Board targetBoard = boardRepo.findOne(boardId);
            if (targetBoard == null) {
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
            } else if(targetBoard.addMember(targetAccount)) {
                boardRepo.save(targetBoard);
                response.setUsername(username);
                response.setUrl(BASE_URL + File.separator + username);
                response.setMessage(MEMBER_ADDED);
                httpResponse.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, USER_ALREADY_MEMBER);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, USER_NOT_FOUND);
        }

        return response;
    }
}
