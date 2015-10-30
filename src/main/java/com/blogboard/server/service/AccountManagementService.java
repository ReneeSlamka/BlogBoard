package com.blogboard.server.service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.web.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class AccountManagementService {

    public final String loginPage = "http://localhost:3000";
    public final String loginSuccess = "http://localhost:3000/home";
    public final String loginFailure = "http://localhost:3000/login-failure";
    public final String accessDenied = "http://localhost:3000/access-denied";
    public final String accountCreationSucessful = "http://localhost:3000/account-creation-succesful";

    //public AccountManagementService() { super(); }

    private final Logger logger = Logger.getLogger(AccountManagementService.class.getName());

    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public LoginResponse validateAccountCreation(String username, String password, String email) {

        LoginResponse response = new LoginResponse();
        response.setHttpResponseHeader(loginFailure);


        Account newAccount = new Account(username, password, email);
        Account savedAccount = accountService.createAccount(newAccount);

        if (savedAccount != null) {
            response.setHttpResponseHeader(loginSuccess);
        } else {
            response.setHttpResponseHeader(loginFailure);
        }

        return response;
    }

    public LoginResponse validateLogin(String username, String password) {

        LoginResponse response = new LoginResponse();

        return response;
    }

    public void validateCredentials(String username, String password, String targetPageLink, LoginResponse response) {

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

    }
}
