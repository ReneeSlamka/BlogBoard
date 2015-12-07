package com.blogboard.server.service;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.web.BasicResponse;
import com.blogboard.server.web.ServiceResponses.AddMemberResponse;
import com.blogboard.server.web.ServiceResponses.CreateBoardResponse;
import com.blogboard.server.web.ServiceResponses.AddPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import com.blogboard.server.data.entity.Account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
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
    private static final String BOARD_NAME_UPDATED = "Board name successfully updated";

    private static final String NAME_IN_USE = "Sorry, it seems there is already a board with that name";
    public static final String BOARD_NOT_FOUND = "Error, board with that name doesn't exist";
    private static final String BOARD_ACCESS_DENIED = "Error, you do not have permission to access this board";
    private static final String USER_NOT_FOUND = "Failed to add new member, account with given username doesn't exist";
    private static final String USER_ALREADY_MEMBER = "This user is already a member of this board";
    private static final String USER_NOT_BOARD_ADMIN = "Action not permitted, you are not the admin of this board";
    private static final String REMOVE_MEMBER_FAILURE = "Failed to remove member, account with given username " +
                                                        "either doesn't exist or isn't a member of this board";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BoardRepository boardRepo;



    /*
    * Method Name: Create Board
    * Purpose: create new board, store in database and return the necessary info to add it to
    * the DOM on the client side
    */

    public CreateBoardResponse createBoard(boolean sessionValid, String boardName, String ownerUsername,
                                           HttpServletResponse httpResponse) throws IOException {

        CreateBoardResponse response = new CreateBoardResponse();
        if (!sessionValid) { return response; }

        String decodedName = AppServiceHelper.decodeString(boardName);
        Account boardOwner = accountRepo.findByUsername(ownerUsername);

        //SUCCESS CASE: board with that name does not already exist (for this user)
        if (boardRepo.findByNameAndOwner(decodedName, boardOwner) == null) {
            //create board and save in board repo
            Board newBoard = new Board(decodedName, boardOwner, AppServiceHelper.createTimeStamp());
            newBoard.addMember(boardOwner);
            Board savedBoard = boardRepo.save(newBoard);
            savedBoard.setUrl(BASE_BOARD_URL);
            savedBoard = boardRepo.save(savedBoard);
            boardOwner.addAccessibleBoard(savedBoard);
            Account savedAccount = accountRepo.save(boardOwner);
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
    * Purpose: return a board so that its information can be rendered in its page
    */

    public Board getBoard(Long boardId, String username, HttpServletResponse httpResponse)
            throws IOException {

        Board targetBoard = boardRepo.findOne(boardId);
        Account targetMember = accountRepo.findByUsername(username);

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
    * Method Name: Edit Board
    * Purpose: edit the name of a board (only owner will be able to do this via UI)
    */

    public BasicResponse editBoard(boolean sessionValid, Long boardId, String username, String newBoardName,
                                   HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }
        Board targetBoard = boardRepo.findOne(boardId);

        if (targetBoard != null) {
            if (targetBoard.getOwner().equals(accountRepo.findByUsername(username))) {
                targetBoard.setName(newBoardName);
                Board savedBoard = boardRepo.save(targetBoard);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                response.setMessage(BOARD_NAME_UPDATED);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, USER_NOT_BOARD_ADMIN);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
        }

        return response;
    }


    /*
    * Method Name: Delete Board
    * Purpose: edit the name of a board (only owner will be able to do this via UI)
    */

    public BasicResponse deleteBoard(boolean sessionValid, Long boardId, String username,
                                   HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }
        Board targetBoard = boardRepo.findOne(boardId);
        Account targetAccount = accountRepo.findByUsername(username);

        if (targetBoard != null) {
            if (targetBoard.getOwner().equals(accountRepo.findByUsername(username)) &&
                    targetAccount.removeAdminLevelBoard(targetBoard)) {
                        List<Account> listMembers = targetBoard.getMembers();
                        for (Account member: listMembers) {
                            member.removeAccessibleBoard(targetBoard);
                        }
                        boardRepo.delete(boardId);
                        //Todo: might need to manually remove from user board lists and save
            } else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, USER_NOT_BOARD_ADMIN);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
        }

        return response;
    }

    /*
   * Method Name: Get List Boards
   * Purpose: to retrieve all boards that either belong to that user or that user is a member of
   */

    public ModelAndView getHomePageBoardsList(boolean sessionValid, String username,
                                              HttpServletResponse httpResponse) throws IOException {

        ModelAndView mav = new ModelAndView();
        if (!sessionValid) { return mav; }
        Account user = accountRepo.findByUsername(username);

        List<Account> memberSearchList = new ArrayList<Account>();
        memberSearchList.add(user);
        List<Board> createdBoards = user.getAdminLevelBoards();
        List<Board> accessibleBoards = user.getAccessibleBoards();
        List<Board> memberBoards = new ArrayList<Board>();

        for (Board board : accessibleBoards) {
            if (!board.getOwner().equals(user)) {
                memberBoards.add(board);
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
    * Purpose: To add the username of the new member to a board's members list
    */

    public AddMemberResponse addMember(boolean sessionValid, String username, Long boardId,
                                       HttpServletResponse httpResponse) throws IOException {

        AddMemberResponse response = new AddMemberResponse();
        if (!sessionValid) { return response; }

        Account targetAccount = accountRepo.findByUsername(username);

        if (targetAccount != null) {
            Board targetBoard = boardRepo.findOne(boardId);
            if (targetBoard == null) {
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BOARD_NOT_FOUND);
            } else if(targetBoard.addMember(targetAccount)) {
                targetAccount.addAccessibleBoard(targetBoard);
                Account savedAccount = accountRepo.save(targetAccount);
                Board savedBoard = boardRepo.save(targetBoard);
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
