package com.blogboard.server.web;

import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.service.*;
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
public class AccountServicesController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private SessionRepository sessionRepo;


    private static final String BASE_URL = "http://localhost:8080";
    private static final Logger logger = Logger.getLogger(AccountServicesController.class.getName());


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

        return accountService.createAccount(username, password, email, httpResponse);
    }

    /*
    *========== Change Account Email Address ==========
    */

    @RequestMapping(value = "/{username}/account/email", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse changeEmail(
            @RequestParam(value = "newEmailAddress", required = true) String newEmailAddress,
            @CookieValue(value = "sessionID", required = true) String sessionId,
            @CookieValue(value = "sessionUsername", required = true) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return accountService.changeEmail(sessionValid, sessionUsername, newEmailAddress, httpResponse);
    }


    /*
    *========== Change Account Password ==========
    */

    @RequestMapping(value = "/{username}/account/password", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse changePassword(
            @RequestParam(value = "oldPassword", required = true) String oldPassword,
            @RequestParam(value = "newPassword", required = true) String newPassword,
            @CookieValue(value = "sessionID", required = true) String sessionId,
            @CookieValue(value = "sessionUsername", required = true) String sessionUsername,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return accountService.changePassword(sessionValid, sessionUsername, oldPassword, newPassword, httpResponse);
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

        return authenticationService.login(username, password, httpResponse);
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

        return authenticationService.logout(sessionUsername, sessionId, httpResponse);
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

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return boardService.getHomePageBoardsList(sessionValid, sessionUsername, httpResponse);
    }
}