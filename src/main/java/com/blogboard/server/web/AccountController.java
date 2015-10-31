package com.blogboard.server.web;

import com.blogboard.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    CreateAccountResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password){

        return accountService.createAccount(username, password);
    }*/
}