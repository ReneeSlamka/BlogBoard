package com.blogboard.server.service;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.web.BasicResponse;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Service
public class AuthenticationService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_PAGE = BASE_URL + File.separator + "login";

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


    /*
   * Method Name: Login
   * Inputs: Account Repository, Session Repository, username, password, HTTP Servlet BasicResponse
   * Return Value: Account Services BasicResponse
   * Purpose: logs user in by creating a session object, storing it the database and returning its values
   * in a cookie to be stored on the client side for persistent authentication
    */

    //Todo: break down this method into into smaller submethods
    public BasicResponse login(AccountRepository accountRepo, SessionRepository sessionRepo, String username,
                                        String password, HttpServletResponse httpResponse) throws IOException {

        BasicResponse response = new BasicResponse();
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
    * Inputs: Session Repository, username, sessionId, HTTP Servlet BasicResponse
    * Return Value: Account Services BasicResponse
    * Purpose: logs user out of their current session, deletes their session from the database and returns
    * the url to the login page to redirect the client
     */
    public BasicResponse logout(SessionRepository sessionRepo, String sessionUsername, String sessionID,
                                         HttpServletResponse httpResponse) throws IOException {
        BasicResponse response = new BasicResponse();

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
    * Method Name: Validate Session
    * Inputs: sessionId, sessionUsername
    * Return Value: boolean
    * Purpose: Checks if current session is valid before every service call and that user tied
    * to it exists in DB. Also sets appropriate error status and message if an error is found.
     */

    public boolean validateSession(AccountRepository accountRepo, SessionRepository sessionRepo, String sessionId,
                                   String sessionUsername, HttpServletResponse httpResponse) throws IOException {

        Session targetSession = sessionRepo.findBySessionId(AppServiceHelper.hashString(sessionId));
        httpResponse.setHeader("Location", LOGIN_PAGE);

        if(sessionId.isEmpty() || targetSession != null) {
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

    /*
    * Method Name: Validate User Session
    * Inputs: Session Repository, HTTP Servlet BasicResponse, sessionId
    * Return Value: Account Services BasicResponse
    * Purpose: checks if sessionId provided by client's cookie matches with one stored in database and returns
     * success message with user home page url to redirect client to if it does
     */
    public BasicResponse validateUserSession(SessionRepository sessionRepo,
                                                      HttpServletResponse httpResponse, String sessionId, String sessionUsername) throws IOException{

        BasicResponse response = new BasicResponse();
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
