package com.blogboard.server.service;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.web.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Service
public class AuthenticationService {

    private static final String BASE_URL = "http://localhost:8080";

    private static final String LOGIN_SUCCESSFUL = "Login successful!";
    private static final String LOGOUT_SUCCESSFUL = "Logout successful";
    private static final String SESSION_VALID = "Valid login session found";
    private static final String INCORRECT_USERNAME = "Sorry, it seems that there is no account with that username.";
    private static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login attempt";
    private static final String INCORRECT_PASSWORD = "Sorry, it seems that password is incorrect.";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";
    private static final String INVALID_SESSION = "Not a valid session";
    private static final String NO_SESSION_FOUND = "No session has been initialized";
    private static final String USER_NOT_FOUND = "Failed to add new member, account with given username doesn't exist";


    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private SessionRepository sessionRepo;


    /*
   * Method Name: Login
   * Purpose: logs user in by creating a session object, storing it the database and returning its values
   * in a cookie to be stored on the client side for persistent authentication
    */

    public BasicResponse login(String username, String password, HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
        Account targetAccount = accountRepo.findByUsername(username);
        Boolean loginPermitted = false;

        //Check if account exists and whether password is correct
        if (targetAccount != null && targetAccount.getPassword().equals(AppServiceHelper.hashString(password))){
            Session previousSession = sessionRepo.findByAccountUsername(username);
            //SUCCESS CASE: credentials correct and no previous session exists in database
            if (previousSession == null) {
                loginPermitted = true;
            //CASE: user trying to login without session credentials while previous session still stored in DB
            } else {
                String currentTime = AppServiceHelper.createTimeStamp();
                boolean validExpirationTime =
                        AppServiceHelper.validateExpirationTime(previousSession.getTimeStamp(), currentTime);
                //SUCCESS CASE: session cookie expired on client before user manually logged out
                if (validExpirationTime) {
                    loginPermitted = true;
                //FAILURE CASE: someone trying to login to same account while session still in place
                } else {
                    httpResponse.setHeader("Location", BASE_URL);
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_LOGIN_ATTEMPT);
                }
            }
        } else {
            //FAILURE CASE(S): either credentials were wrong or unknown error occurred
            httpResponse.setHeader("Location", BASE_URL);
            if (accountRepo.findByUsername(username) == null) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INCORRECT_USERNAME);
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INCORRECT_PASSWORD);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            }
        }

        if (loginPermitted) {
            activateSession(username, response, httpResponse);
        }
        return response;
    }


    /*
    * Method Name: Activate Session
    * Purpose: Helper function generate and save a new session, and configure session cookies
     */

    public void activateSession(String username, BasicResponse response, HttpServletResponse httpResponse) {

        String newSessionId = AppServiceHelper.generateSessionID();
        String hashedSessionId = AppServiceHelper.hashString(newSessionId);
        String timeStamp = AppServiceHelper.createTimeStamp();
        Session newSession = new Session(username, hashedSessionId, timeStamp);
        Session savedSession = sessionRepo.save(newSession);

        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.setHeader("Location", BASE_URL + File.separator + username);

        Cookie sessionId = new Cookie("sessionID", newSessionId);
        AppServiceHelper.configureCookie(sessionId, (60 * 30), "/", false, false);
        httpResponse.addCookie(sessionId);
        Cookie sessionUsername = new Cookie("sessionUsername", username);
        AppServiceHelper.configureCookie(sessionUsername, (60 * 30), "/", false, false);
        httpResponse.addCookie(sessionUsername);
        response.setMessage(LOGIN_SUCCESSFUL);
    }


    /*
    * Method Name: Logout
    * Purpose: logs user out of their current session, deletes their session from the database and returns
    * the url to the login page to redirect the client
     */

    public BasicResponse logout(String sessionUsername, String sessionID, HttpServletResponse httpResponse)
            throws IOException {

        BasicResponse response = new BasicResponse();

        if (sessionID.equals("undefined") || sessionID.length() == 0){
            httpResponse.setHeader("Location", BASE_URL);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_SESSION_FOUND);
            return response;
        }

        Session targetSesssion = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionID));

        //if sessionID exists and is valid remove session from DB
        if (targetSesssion != null && targetSesssion.getAccountUsername().equals(sessionUsername)) {
            sessionRepo.delete(targetSesssion.getId());
            httpResponse.setHeader("Location", BASE_URL);
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
    * Method Name: Validate Session
    * Purpose: Checks if current session is valid before every service call and that user tied
    * to it exists in DB. Also sets appropriate error status and message if an error is found.
     */

    public boolean validateSession(String sessionId, String sessionUsername, HttpServletResponse httpResponse)
            throws IOException {

        Session targetSession = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionId));
        httpResponse.setHeader("Location", BASE_URL);

        if(!sessionId.isEmpty() && !sessionUsername.isEmpty() && targetSession != null) {
            if(targetSession.getAccountUsername().equals(sessionUsername)) {
                if (accountRepo.findByUsername(sessionUsername) != null) {
                    httpResponse.setHeader("Location", BASE_URL + File.separator + sessionUsername);
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    return true;
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, USER_NOT_FOUND);
                }
            } else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_SESSION);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, NO_SESSION_FOUND);
        }

        return false;
    }
}