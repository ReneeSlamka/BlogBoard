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
    private static final String EMAIL_CHANGED = "Email successfully changed";
    private static final String PASSWORD_CHANGED = "Password successfully changed";
    private static final String USERNAME_IN_USE = "Sorry, it seems there is already an account with that username";
    private static final String EMAIL_IN_USE = "Sorry, it seems there is already an account with that email";
    private static final String INCORRECT_PASSWORD = "Password change failed, old password incorrect";
    private static final String PASSWORD_IN_USE = "New password must be different from old password";
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
            Account newAccount = new Account(username, AppServiceHelper.hashString(password), email,
                    AppServiceHelper.createTimeStamp());
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

    //Todo: need better security for this
    public BasicResponse changeEmail(boolean sessionValid, String username, String newEmailAddress,
                                     HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }

        Account targetAccount = accountRepo.findByUsername(username);
        if (!targetAccount.getEmail().equals(newEmailAddress) && accountRepo.findByEmail(newEmailAddress) == null) {
            targetAccount.setEmail(newEmailAddress);
            Account savedAccount = accountRepo.save(targetAccount);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            response.setMessage(EMAIL_CHANGED);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_CONFLICT, EMAIL_IN_USE);
        }

        return response;
    }

    /*
    * Method Name: Update Password
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicResponse w/ HTTP Servlet BasicResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    //Todo: need better security for this
    public BasicResponse changePassword(boolean sessionValid, String username, String oldPassword, String newPassword,
                                        HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }
        Account targetAccount = accountRepo.findByUsername(username);

        if(targetAccount.getPassword().equals(AppServiceHelper.hashString(oldPassword))) {
            if (!AppServiceHelper.hashString(newPassword).equals(AppServiceHelper.hashString(oldPassword))) {
                targetAccount.setPassword(AppServiceHelper.hashString(newPassword));
                Account savedAccount = accountRepo.save(targetAccount);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                response.setMessage(PASSWORD_CHANGED);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, PASSWORD_IN_USE);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INCORRECT_PASSWORD);
        }

        return response;
    }
}