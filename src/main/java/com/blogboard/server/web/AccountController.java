package com.blogboard.server.web;

import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.service.AccountManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private AccountManagementService accountManagementService;
    @Autowired
    public void setAccountRepository(AccountManagementService accountManagementService) {
        this.accountManagementService = accountManagementService;
    }


    @RequestMapping("/create-account")

    public @ResponseBody LoginResponse createAccount(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password,
            @RequestParam(value="email", required=false, defaultValue="") String email){

        return accountManagementService.validateAccountCreation(username, password, email);

    }

    @RequestMapping("/login")
    public @ResponseBody
    LoginResponse login(
            @RequestParam(value="username", required=true) String username,
            @RequestParam(value="password", required=true) String password){

        return accountManagementService.validateLogin(username, password);
    }
}