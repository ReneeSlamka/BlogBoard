package com.blogboard.server.web;

import com.blogboard.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    private AccountService accountService;
    @Autowired
    public void setAccountRepository(AccountService accountManagementService) {
        this.accountService = accountManagementService;
    }


    @RequestMapping("/create-account")

    public @ResponseBody
    CreateAccountResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email){

        return accountService.createAccount(username, password, email);

    }

    /*@RequestMapping("/login")
    public @ResponseBody
    LoginResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password){

        return accountService.login(username, password);
    }*/

    @RequestMapping("/login")
    public @ResponseBody
    LoginResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            HttpServletResponse loginResponse){

        return accountService.login(username, password, loginResponse);
    }

    @RequestMapping("/validate-session")
    public @ResponseBody
    ValidateUserSessionResponse validateSession(
            @RequestParam(value="sessionId", required=true) String sessionId) {
        return accountService.validateUserSession(sessionId);
    }
}