package com.blogboard.server.web;


import com.blogboard.server.data.entity.Board;
import com.blogboard.server.service.AuthenticationService;
import com.blogboard.server.service.BoardService;
import com.blogboard.server.web.ServiceResponses.AddMemberResponse;
import com.blogboard.server.web.ServiceResponses.CreateBoardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class BoardServiceController {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BoardService boardService;

    private static final Logger logger = Logger.getLogger(BoardServiceController.class.getName());


    /*
    *========== Create Board ==========
    */

    @RequestMapping(value = "/{username}/boards", method = RequestMethod.POST)
    public
    @ResponseBody
    CreateBoardResponse createBoard(
            @PathVariable String username,
            @RequestParam(value = "boardName", required = true) String boardName,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return boardService.createBoard(sessionValid, boardName, sessionUsername, httpResponse);
    }


    /*
    *========== Edit Board ==========
    */

    @RequestMapping(value = "/{username}/boards/{boardId}", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse editBoard(
            @PathVariable String username,
            @PathVariable Long boardId,
            @RequestParam(value = "editedBoardName", required = true) String editedBoardName,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return boardService.editBoard(sessionValid, boardId, username, editedBoardName, httpResponse);
    }


    /*
    *========== Delete Board ==========
    */

    @RequestMapping(value = "/{username}/boards/{boardId}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    BasicResponse deleteBoard(
            @PathVariable String username,
            @PathVariable Long boardId,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return boardService.deleteBoard(sessionValid, boardId, username, httpResponse);
    }


    //Todo: need to refactor, move code body to a function in services
    /*
    *========== Get Board Page ==========
    */

    @RequestMapping(value = "/boards/{boardId}", method = RequestMethod.GET)
    public ModelAndView getBoardPage(
            @PathVariable Long boardId,
            @CookieValue(value = "sessionID", defaultValue = "", required = false)
            String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false)
            String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        ModelAndView mav = new ModelAndView();
        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        if (!sessionValid) { return mav; }
        Board targetBoard = boardService.getBoard(boardId, sessionUsername, httpResponse);

        //create object to add to pebble board template
        mav.addObject("boardName", targetBoard.getName());
        mav.addObject("boardOwner", targetBoard.getOwner().getUsername());
        mav.addObject("boardId", targetBoard.getId());
        mav.addObject("dateCreated", targetBoard.getDateCreated());
        mav.addObject("boardMembers", targetBoard.getMembers());
        mav.addObject("boardPosts", targetBoard.getPosts());
        mav.setViewName("board");
        return mav;
    }


    /*
    *========== Add Member ==========
    */

    @RequestMapping(value = "/boards/{boardId}/members", method = RequestMethod.POST)
    public
    @ResponseBody
    AddMemberResponse addMember(
            @PathVariable Long boardId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @RequestParam(value = "memberUsername", required = true) String memberUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return boardService.addMember(sessionValid, memberUsername, boardId, httpResponse);
    }
}
