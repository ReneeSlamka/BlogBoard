package com.blogboard.server.web;

import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.data.repository.PostRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.service.*;
import com.blogboard.server.web.ServiceResponses.AddMemberResponse;
import com.blogboard.server.web.ServiceResponses.CreateBoardResponse;
import com.blogboard.server.web.ServiceResponses.AddPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ServicesController {

    private AccountService accountService;
    private BoardService boardService;
    private AuthenticationService authenticationService;
    private PostService postService;
    private static final String BASE_URL = "http://localhost:8080";

    //TODO: in future will have to ensure repositories are only accessed by one call at a time
    //will need resource locking
    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;
    private BoardRepository boardRepo;
    private PostRepository postRepo;

    @Autowired(required = true)
    public void setSessionRepository(BoardRepository boardRepository) {
        this.boardRepo = boardRepository;
    }

    @Autowired(required = true)
    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }

    @Autowired(required = true)
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired(required = true)
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired(required = true)
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired(required = true)
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired(required = true)
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }

    @Autowired(required = true)
    public void setPostRepository(PostRepository postRepository) {
        this.postRepo = postRepository;
    }


    private static final Logger logger = Logger.getLogger(ServicesController.class.getName());


    /*
    *========== Get Index Page ==========
    */

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getIndexPage(
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        httpResponse.setStatus(HttpServletResponse.SC_OK);

        if (!sessionId.isEmpty() && !sessionUsername.isEmpty()){
            Session targetSession = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionId));
            if (targetSession != null) {
                if (targetSession.getAccountUsername().equals(sessionUsername) &&
                        accountRepo.findByUsername(sessionUsername) != null) {
                    httpResponse.sendRedirect(BASE_URL + File.separator + sessionUsername);
                }
            }
        }

        return mav;
    }

    /*
    *========== Create Account ==========
    */
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse createAccount(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            HttpServletResponse httpResponse) throws IOException {

        return accountService.createAccount(accountRepo, username, password, email, httpResponse);
    }


    /*
    *========== Login ==========
    */
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public
    @ResponseBody
    BasicResponse login(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            HttpServletResponse httpResponse) throws IOException {

        return authenticationService.login(accountRepo, sessionRepo, username, password, httpResponse);
    }


    /*
    *========== Logout ==========
    */
    @RequestMapping(value = "/sessions", method = RequestMethod.POST) //better http call option?
    public
    @ResponseBody
    BasicResponse logout(
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        return authenticationService.logout(sessionRepo, sessionUsername, sessionId, httpResponse);
    }


    /*
    *========== Get Home Page ==========
    */
    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ModelAndView getHomePage(
            @PathVariable String username,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false)
            String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false)
            String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);
        return boardService.getHomePageBoardsList(boardRepo, accountRepo, sessionValid, sessionUsername, httpResponse);
    }


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

        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);
        return boardService.createBoard(boardRepo,accountRepo, sessionValid, boardName, sessionUsername, httpResponse);
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
        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);
        if (!sessionValid) {
            return mav;
        }
        Board targetBoard = boardService.getBoard(boardRepo, accountRepo, boardId, sessionUsername, httpResponse);

        //create object to add to pebble board template
        mav.addObject("boardName", targetBoard.getName());
        mav.addObject("boardOwner", targetBoard.getOwner().getUsername());
        //mav.addObject("boardId", targetBoard.getId());
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

        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);
        return boardService.addMember(accountRepo, boardRepo, sessionValid, memberUsername, boardId, httpResponse);
    }


    /*
    *========== Add Post ==========
    */
    @RequestMapping(value = "/boards/{boardId}/posts", method = RequestMethod.POST)
    public
    @ResponseBody
    AddPostResponse addPost(
            @PathVariable Long boardId,
            @RequestParam(value = "title",defaultValue = "", required = false) String title,
            @RequestParam(value = "textBody",defaultValue = "", required = false) String textBody,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);

        return postService.addPost(accountRepo, boardRepo, postRepo,sessionValid, boardId, sessionUsername, title,
                textBody, httpResponse);
    }


    /*
    *========== Edit Post ==========
    */
    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.PUT)
    public
    @ResponseBody
    BasicResponse editPost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @RequestParam(value = "title",defaultValue = "", required = false) String editedTitle,
            @RequestParam(value = "textBody",defaultValue = "", required = false) String editedTextBody,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(accountRepo, sessionRepo, sessionId,
                sessionUsername, httpResponse);

        return postService.editPost(accountRepo, boardRepo, postRepo, sessionValid, boardId, postId,
                editedTitle, editedTextBody, httpResponse);
    }


    /*
    *========== Delete Post ==========
    */
    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse deletePost(
            @PathVariable Long postId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();

        return response;
    }


    /*
    *========== Exception Handler(s) ==========
    */

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "An IO exception occurred")
    @ExceptionHandler(IOException.class)
    public void exceptionHandlerIO(IOException ex) {
        logger.log(Level.SEVERE, "An IO exception has occurred, most likely due to a close internet connection", ex);
    }
}