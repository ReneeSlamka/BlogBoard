package com.blogboard.server.web;

import com.blogboard.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ServicesController {

    private AccountService accountService;
    @Autowired
    public void setAccountRepository(AccountService accountManagementService) {
        this.accountService = accountManagementService;
    }


    @RequestMapping(value ="/account", method=RequestMethod.POST)
    public @ResponseBody
    AccountServiceResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email,
            HttpServletResponse createAccountResponse){

        return accountService.createAccount(username, password, email, createAccountResponse);
    }

    @RequestMapping(value ="/account", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            HttpServletResponse loginResponse){

        return accountService.login(username, password, loginResponse);
    }

    @RequestMapping(value = "/session", method=RequestMethod.GET)
    public @ResponseBody
    AccountServiceResponse validateSession(
            @CookieValue(value = "sessionID", defaultValue = "undefined", required = false) String validationCookie,
            HttpServletResponse validationResponse) {
        return accountService.validateUserSession(validationResponse,validationCookie);
    }


    @RequestMapping(value = "/board", method=RequestMethod.POST)
    public @ResponseBody
    CreateBoardResponse createBoard(
            @RequestParam(value="boardName", required=true) String boardName,
            @CookieValue(value = "sessionUsername", defaultValue = "undefined", required = false) String usernameCookie,
            HttpServletResponse createAccountServletResponse) {
        return accountService.createBoard(boardName, usernameCookie, createAccountServletResponse);
    }

}