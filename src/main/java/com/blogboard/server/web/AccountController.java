package com.blogboard.server.web;

import com.blogboard.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    private AccountService accountService;
    @Autowired
    public void setAccountRepository(AccountService accountManagementService) {
        this.accountService = accountManagementService;
    }


    @RequestMapping(value ="/account", method=RequestMethod.POST)
    public @ResponseBody
    CreateAccountResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email,
            HttpServletResponse createAccountResponse){

        return accountService.createAccount(username, password, email, createAccountResponse);
    }

    @RequestMapping(value ="/account", method=RequestMethod.GET)
    public @ResponseBody
    LoginResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            HttpServletResponse loginResponse){

        return accountService.login(username, password, loginResponse);
    }

    @RequestMapping(value = "/session", method=RequestMethod.GET)
    public @ResponseBody
    ValidateUserSessionResponse validateSession(
            @RequestParam(value="sessionId", required=true) String sessionId,
            @CookieValue(value = "sessionIdCookie", defaultValue = "undefined", required = false) String validationCookie,
            HttpServletResponse validationResponse) {
        return accountService.validateUserSession(sessionId, validationResponse,validationCookie);
    }
}