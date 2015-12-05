package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Service
public class AccountService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_PAGE = BASE_URL + File.separator + "login";

    //Custom Success/Error Messages
    private static final String ACCOUNT_CREATED = "Congrats, your account has successfully been " +
                                                                "created! You can now login and start blogging.";
    private static final String USERNAME_IN_USE = "Sorry, it seems there is already an account with that username";
    private static final String EMAIL_IN_USE = "Sorry, it seems there is already an account with that email";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";


    @Autowired
    private AccountRepository accountRepo;


    /*
    * Method Name: Create Account
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicResponse w/ HTTP Servlet BasicResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public BasicResponse createAccount(String username, String password, String email, HttpServletResponse httpResponse)
            throws IOException{

        BasicResponse response = new BasicResponse();

        //SUCCESS CASE: account with provided credentials doesn't already exist
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {
            Account newAccount = new Account(username, AppServiceHelper.hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);
            response.setMessage(ACCOUNT_CREATED);

            //FAILURE CASE: unexpected repository error
            if (savedAccount == null) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_PAGE);
            }
        //FAILURE CASE(S): either account with same credential(s) already exists or unknown error occurred
        } else {
            httpResponse.setHeader("Location", LOGIN_PAGE);
            if(accountRepo.findByUsername(username) != null) {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, USERNAME_IN_USE);
            } else if (accountRepo.findByEmail(email) != null) {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, EMAIL_IN_USE);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            }
        }

        return response;
    }

    /*
    * Method Name: Update Email
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicResponse w/ HTTP Servlet BasicResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public BasicResponse updateEmail() {
        return new BasicResponse();
    }

    /*
    * Method Name: Update Password
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicResponse w/ HTTP Servlet BasicResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public BasicResponse updatePassword() {
        return new BasicResponse();
    }
}
