package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.*;
import com.blogboard.server.data.repository.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.http.HttpStatus;

@Service
public class AccountService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_SUCCESS_URL = "http://localhost:8080/home";
    private static final String LOGIN_PAGE = "http://localhost:8080/login";


    //Custom Success/Error Messages
    private static final String ACCOUNT_CREATED = "Congrats, your account has successfully been " +
                                                                "created! You can now login and start blogging.";
    private static final String USERNAME_IN_USE = "Sorry, it seems there is already an account with that username";
    private static final String EMAIL_IN_USE = "Sorry, it seems there is already an account with that email";
    private static final String LOGIN_SUCCESSFUL = "Login successful!";
    private static final String INCORRECT_USERNAME = "Sorry, it seems that there is no account with that username.";
    private static final String INCORRECT_PASSWORD = "Sorry, it seems that password is incorrect.";
    private static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login attempt";
    private static final String LOGOUT_SUCCESSFUL = "Logout successful";
    private static final String SESSION_VALID = "Valid login session found";
    private static final String NO_SESSION_FOUND = "No session has been initialized";
    private static final String INVALID_SESSION = "Not a valid session";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";


    /*
    * Method Name: Create Account
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicAPIResponse w/ HTTP Servlet BasicAPIResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public AccountServiceResponse createAccount(AccountRepository accountRepo, String username, String password,
        String email, HttpServletResponse httpResponse) throws IOException{
        AccountServiceResponse response = new AccountServiceResponse();

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
    * Method Name: Login
    * Inputs: Account Repository, Session Repository, username, password, HTTP Servlet BasicAPIResponse
    * Return Value: Account Services BasicAPIResponse
    * Purpose: logs user in by creating a session object, storing it the database and returning its values
    * in a cookie to be stored on the client side for persistent authentication
     */
    public AccountServiceResponse login(AccountRepository accountRepo, SessionRepository sessionRepo, String username,
        String password, HttpServletResponse httpResponse) throws IOException{

        AccountServiceResponse response = new AccountServiceResponse();
        Account targetAccount = accountRepo.findByUsername(username);


        //Check if account exists and whether password is correct
        if (targetAccount != null && targetAccount.getPassword().equals(AppServiceHelper.hashString(password))){

            Session previousSession = sessionRepo.findByAccountUsername(username);
            //SUCCESS CASE: no previous session exists in database
            if (previousSession == null) {
                String newSessionId = AppServiceHelper.generateSessionID();
                String hashedSessionId = AppServiceHelper.hashString(newSessionId);
                String timeStamp = AppServiceHelper.createTimeStamp();
                Session newSession = new Session(username, hashedSessionId, timeStamp);
                Session savedSession = sessionRepo.save(newSession);

                httpResponse.setStatus(HttpServletResponse.SC_OK);
                //return getHomePage url --> /{username}
                httpResponse.setHeader("Location", BASE_URL + File.separator + username);

                Cookie sessionId = new Cookie("sessionID", newSessionId);
                AppServiceHelper.configureCookie(sessionId, (60 * 15), "/", false, false);
                httpResponse.addCookie(sessionId);
                Cookie sesssionUsername = new Cookie("sessionUsername", username);
                AppServiceHelper.configureCookie(sesssionUsername, (60 * 15), "/", false, false);
                httpResponse.addCookie(sesssionUsername);
                response.setMessage(LOGIN_SUCCESSFUL);

                //session cookie expired before user logged out
            } else {
                String timeStamp = AppServiceHelper.createTimeStamp();
                //substract current time against time is previous session
                //if is greater than 30 minutes can still allow user to login again

                httpResponse.setHeader("Location", LOGIN_PAGE);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_LOGIN_ATTEMPT);
            }

            Account updatedAccount = accountRepo.save(targetAccount);//Todo: remove this?
        } else {
            //FAILURE CASE(S): either credentials were wrong or unknown error occurred
            httpResponse.setHeader("Location", LOGIN_PAGE);

            if (accountRepo.findByUsername(username) == null) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INCORRECT_USERNAME);
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INCORRECT_PASSWORD);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            }
        }

        return response;
    }


    /*
    * Method Name: Logout
    * Inputs: Session Repository, username, sessionId, HTTP Servlet BasicAPIResponse
    * Return Value: Account Services BasicAPIResponse
    * Purpose: logs user out of their current session, deletes their session from the database and returns
    * the url to the login page to redirect the client
     */
    public AccountServiceResponse logout(SessionRepository sessionRepo, String sessionUsername, String sessionID,
        HttpServletResponse httpResponse) throws IOException {
        AccountServiceResponse response = new AccountServiceResponse();

        if (sessionID.equals("undefined") || sessionID.length() == 0){
            httpResponse.setHeader("Location", LOGIN_PAGE);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_SESSION_FOUND);
            return response;
        }

        Session targetSesssion = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionID));

        //if sessionID exists and is valid remove session from DB
        if (targetSesssion != null && targetSesssion.getAccountUsername().equals(sessionUsername)) {
            sessionRepo.delete(targetSesssion.getId());
            httpResponse.setHeader("Location", LOGIN_PAGE);
            response.setMessage(LOGOUT_SUCCESSFUL);
        } else if (targetSesssion == null) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_SESSION_FOUND);
        } else if (!targetSesssion.getAccountUsername().equals(sessionUsername)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_SESSION);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
        }

        return response;
    }


    /*
    * Method Name: Validate User Session
    * Inputs: Session Repository, HTTP Servlet BasicAPIResponse, sessionId
    * Return Value: Account Services BasicAPIResponse
    * Purpose: checks if sessionId provided by client's cookie matches with one stored in database and returns
     * success message with user home page url to redirect client to if it does
     */
    public AccountServiceResponse validateUserSession(SessionRepository sessionRepo,
        HttpServletResponse httpResponse, String sessionId, String sessionUsername) throws IOException{

        AccountServiceResponse response = new AccountServiceResponse();
        Session targetSession = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionId));

        //TODO: improve security here after more research
        if ((!sessionId.isEmpty()) && (targetSession != null)) {
            if (targetSession.getAccountUsername().equals(sessionUsername)) {
                httpResponse.setHeader("Location", BASE_URL + File.separator + sessionUsername);
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                response.setMessage(SESSION_VALID);
            } else {
                httpResponse.setHeader("Location", LOGIN_PAGE);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_SESSION);
            }
        } else {
            httpResponse.setHeader("Location", LOGIN_PAGE);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_SESSION_FOUND);
        }

        return response;
    }
}
