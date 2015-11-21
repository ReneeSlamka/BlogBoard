package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.*;
import com.blogboard.server.data.repository.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class AccountService {

    private static final String LOGIN_SUCCESS_URL = "http://localhost:8080/home";
    private static final String LOGIN_PAGE = "http://localhost:8080/login";

    private static final String ACCOUNT_CREATION_SUCCESS = "Congrats, your account has successfully been " +
                                                                "created! You can now login and start blogging.";
    private static final String ACCOUNT_CREATION_FAILURE_USERNAME = "Sorry, it seems there is already an account with that username";
    private static final String ACCOUNT_CREATION_FAILURE_EMAIL = "Sorry, it seems there is already an account with that email";
    private static final String LOGIN_SUCCESS = "Login successful!";
    private static final String LOGIN_FAILURE_USERNAME = "Sorry, it seems that there is no account with that username.";
    private static final String LOGIN_FAILURE_PASSWORD = "Sorry, it seems that password is incorrect.";
    private static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login attempt";
    private static final String LOGOUT_SUCCESS = "Logout successful";
    private static final String SESSION_VALID = "Valid login session created";
    private static final String NO_SESSION_FOUND = "No session has been initialized";
    private static final String INVALID_SESSION = "Not a valid session";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";

    public enum Service {
        ACCOUNT_CREATION, LOGIN, LOGOUT, VALIDATION
    }

    /*
    * Method Name: Create Account
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services Response w/ HTTP Servlet Response
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public AccountServiceResponse createAccount(AccountRepository accountRepo, String username, String password,
        String email, HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.ACCOUNT_CREATION);

        //account with provided credentials doesn't already exist
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {
            Account newAccount = new Account(username, AppServiceHelper.hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);
            response.setToSuccess();
            if (savedAccount == null) {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        UNKNOWN_ERROR
                );
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_PAGE);
            }
        //either account with same credential(s) already exists or unknown error occurred
        } else {
            httpResponse.setHeader("Location", LOGIN_PAGE);
            if(accountRepo.findByUsername(username) != null) {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_CONFLICT,
                        ACCOUNT_CREATION_FAILURE_USERNAME
                );
            } else if (accountRepo.findByEmail(email) != null) {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_CONFLICT,
                        ACCOUNT_CREATION_FAILURE_EMAIL
                );
            } else {
                //to cover other unknown errors
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        UNKNOWN_ERROR
                );
            }
        }

        return response;
    }

    /*
    * Method Name: Login
    * Inputs: Account Repository, Session Repository, username, password, HTTP Servlet Response
    * Return Value: Account Services Response
    * Purpose: logs user in by creating a session object, storing it the database and returning its values
    * in a cookie to be stored on the client side for persistent authentication
     */
    public AccountServiceResponse login(AccountRepository accountRepo, SessionRepository sessionRepo, String username,
        String password, HttpServletResponse httpResponse) {

        AccountServiceResponse response = new AccountServiceResponse(Service.LOGIN);
        Account targetAccount = accountRepo.findByUsername(username);

        //Check if account exists and whether password is correct
        if (targetAccount != null && targetAccount.getPassword().equals(AppServiceHelper.hashString(password))){
            //generate session id and store it to this account in db
            Session previousSession = sessionRepo.findByAccountUsername(username);
            if (previousSession == null) {
                String newSessionId = AppServiceHelper.generateSessionID();
                Session newSession = new Session(
                        username,
                        AppServiceHelper.hashString(newSessionId),
                        AppServiceHelper.createTimeStamp());
                Session savedSession = sessionRepo.save(newSession);

                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);

                Cookie sessionId = new Cookie("sessionID", newSessionId);
                AppServiceHelper.configureCookie(sessionId, (60 * 15), "/", false, false);
                httpResponse.addCookie(sessionId);
                Cookie sesssionUsername = new Cookie("sessionUsername", username);
                AppServiceHelper.configureCookie(sesssionUsername, (60 * 15), "/", false, false);
                httpResponse.addCookie(sesssionUsername);
                response.setToSuccess();

                //session cookie expired before user logged out
            } else {
                httpResponse.setHeader("Location", LOGIN_PAGE);
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        INVALID_LOGIN_ATTEMPT
                );
            }

            Account updatedAccount = accountRepo.save(targetAccount);
        }
        else {
            //either credentials were wrong or unknown error occurred
            httpResponse.setHeader("Location", LOGIN_PAGE);

            if (accountRepo.findByUsername(username) == null) {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        LOGIN_FAILURE_USERNAME
                );
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        LOGIN_FAILURE_PASSWORD
                );
            } else {
                AppServiceHelper.configureHttpError(
                        httpResponse,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        UNKNOWN_ERROR
                );
            }
        }

        return response;
    }


    /*
    * Method Name: Logout
    * Inputs: Session Repository, username, sessionId, HTTP Servlet Response
    * Return Value: Account Services Response
    * Purpose: logs user out of their current session, deletes their session from the database and returns the url
    * to the login page to redirect the client
     */
    public AccountServiceResponse logout(SessionRepository sessionRepo, String username, String sessionID,
        HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.LOGOUT);

        if (sessionID.equals("undefined") || sessionID.length() == 0){
            httpResponse.setHeader("Location", LOGIN_PAGE);
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    NO_SESSION_FOUND
            );
            return response;
        }

        Session targetSesssion = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionID));

        //if sessionID exists and is valid remove session from DB
        if (targetSesssion != null && targetSesssion.getAccountUsername().equals(username)) {
            sessionRepo.delete(targetSesssion.getId());
            httpResponse.setHeader("Location", LOGIN_PAGE);
            response.setToSuccess();
        } else if (targetSesssion == null) {
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    NO_SESSION_FOUND
            );
        } else if (!targetSesssion.getAccountUsername().equals(username)) {
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    INVALID_SESSION
            );
        } else {
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    UNKNOWN_ERROR
            );
        }

        return response;
    }


    /*
    * Method Name: Validate User Session
    * Inputs: Session Repository, HTTP Servlet Response, sessionId
    * Return Value: Account Services Response
    * Purpose: checks if sessionId provided by client's cookie matches with one stored in database and returns
     * success message with user home page url to redirect client to if it does
     */
    public AccountServiceResponse validateUserSession(SessionRepository sessionRepo, HttpServletResponse httpResponse,
        String sessionID) {
        AccountServiceResponse response = new AccountServiceResponse(Service.VALIDATION);

        if (sessionID.equals("undefined") || sessionID.length() == 0){
            httpResponse.setHeader("Location", LOGIN_PAGE);
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    NO_SESSION_FOUND
            );
            return response;
        }

        Session targetAccount = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionID));

        //TODO: improve security here after more research
        if (targetAccount != null){
            httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            response.setToSuccess();
        } else {
            httpResponse.setHeader("Location", LOGIN_PAGE);
            AppServiceHelper.configureHttpError(
                    httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    INVALID_SESSION
            );
        }

        return response;
    }
}
