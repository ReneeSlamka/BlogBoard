package com.blogboard.server.web;

import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.service.BoardService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ServicesController {

    private AccountService accountService;
    private BoardService boardService;

    //TODO: in future will have to ensure repositories are only accessed by one call at a time
    //will need resource locking
    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;
    private BoardRepository boardRepo;

    @Autowired (required = true)
    public void setSessionRepository(BoardRepository boardRepository) {
        this.boardRepo = boardRepository;
    }

    @Autowired (required = true)
    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }

    @Autowired (required = true)
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired (required = true)
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired (required = true)
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }


    private static final Logger logger = Logger.getLogger(ServicesController.class.getName());



    @RequestMapping(value ="/accounts", method=RequestMethod.POST)
    public @ResponseBody
    AccountServiceResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email,
            HttpServletResponse httpResponse) throws IOException {

        return accountService.createAccount(accountRepo, username, password, email, httpResponse);
    }

    @RequestMapping(value ="/accounts", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            HttpServletResponse httpResponse) throws IOException {

        return accountService.login(accountRepo, sessionRepo, username, password, httpResponse);
    }

    @RequestMapping(value ="/sessions", method=RequestMethod.POST) //better http call option?
    public @ResponseBody
    AccountServiceResponse logout(
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        return accountService.logout(sessionRepo, sessionUsername, sessionId, httpResponse);
    }

    @RequestMapping(value = "/sessions", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse validateSession(
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {
        return accountService.validateUserSession(sessionRepo, httpResponse, sessionId, sessionUsername);
    }

    @RequestMapping(value = "/boards", method=RequestMethod.POST)
    public @ResponseBody
    BoardServiceResponse createBoard(
            @RequestParam(value="boardName", required=true) String boardName,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {
        return boardService.createBoard(boardRepo, boardName, sessionUsername, httpResponse);
    }

    @RequestMapping(value ="/home", method=RequestMethod.GET)
    public ModelAndView getHomePage(
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId) {
        ModelAndView mav = new ModelAndView();

        ArrayList<Board> userBoards = boardService.getListBoards(boardRepo, sessionUsername);
        mav.addObject("userBoards", userBoards);
        mav.setViewName("home");
        return mav;
    }

    @RequestMapping(value ="/boards={boardName}", method=RequestMethod.GET)
    public ModelAndView getBoardPage(@PathVariable String boardName,
        @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
        @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
                                     HttpServletResponse httpResponse) throws IOException {
        ModelAndView mav = new ModelAndView();

        //add info from repo
        //1. check sessionId for validity (do later)
        BoardServiceResponse getBoardResponse = boardService.getBoard(boardRepo, boardName, sessionUsername, httpResponse);

        //create object to add to pebble board template
        mav.addObject("boardName", getBoardResponse.getBoard().getName());
        mav.addObject("boardOwner", getBoardResponse.getBoard().getOwnerUsername());
        mav.addObject("dateCreated", getBoardResponse.getBoard().getDateCreated());
        mav.addObject("boardMembers", getBoardResponse.getBoard().getMembers());
        mav.setViewName("board");
        return mav;
    }

    @RequestMapping(value = "/board={boardName}/members", method=RequestMethod.POST)
    public @ResponseBody
    JSONObject addMember(
            @PathVariable String boardName,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @RequestParam(value="memberUsername", required=true) String memberUsername) {

        return boardService.addMember(accountRepo, boardRepo, sessionUsername, sessionId, memberUsername);
    }


    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason = "An IO exception occurred")
    @ExceptionHandler(IOException.class)
    public void exceptionHandler(IOException ex)
    {
        logger.log(Level.SEVERE, "An IO exception has occurred, most likely due to a close internet connection", ex);
    }
}