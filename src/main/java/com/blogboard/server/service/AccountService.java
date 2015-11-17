package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.*;
import com.blogboard.server.data.repository.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

@Service
public class AccountService {

    private static final String LOGIN_SUCCESS_URL = "http://localhost:8080/home";
    private static final String CREATE_ACCOUNT_FAILURE_URL = "http://localhost:8080/login";
    private static final String LOGIN_PAGE = "http://localhost:8080/login";


    public enum CauseOfFailure {
        USERNAME, PASSWORD, EMAIL, DATABASE_ERROR, INVALID_LOGIN, SESSION_DNE,
        INVALID_SESSION, UNKNOWN_ERROR
    }

    public enum Service {
        ACCOUNT_CREATION, LOGIN, LOGOUT, VALIDATION
    }


    public AccountServiceResponse createAccount(AccountRepository accountRepo, String username, String password,
        String email, HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.ACCOUNT_CREATION);

        //check if account with that username and/or email already exists
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {

            Account newAccount = new Account(username, AppServiceHelper.hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);

            if (savedAccount == null) {
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setToFailure(CauseOfFailure.DATABASE_ERROR);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_PAGE);
                response.setToSuccess();
            }
        } else {
            //either account with same credential(s) already exists or unknown error occurred
            httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            httpResponse.setHeader("Location", CREATE_ACCOUNT_FAILURE_URL);

            if(accountRepo.findByUsername(username) != null) {
                response.setToFailure(CauseOfFailure.USERNAME);
            } else if (accountRepo.findByEmail(email) != null) {
                response.setToFailure(CauseOfFailure.EMAIL);
            } else {
                //to cover other unknown errors
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setToFailure(CauseOfFailure.DATABASE_ERROR);
            }
        }
        return response;
    }


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
                Session newSession = new Session(username, AppServiceHelper.hashString(newSessionId));
                Session savedSession = sessionRepo.save(newSession);

                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);

                Cookie newSessionIdCookie = new Cookie("sessionID", newSessionId);
                AppServiceHelper.configureCookie(newSessionIdCookie, (60 * 10), "/", false, false);
                httpResponse.addCookie(newSessionIdCookie);

                Cookie newSesssionUsernameCookie = new Cookie("sessionUsername", username);
                AppServiceHelper.configureCookie(newSesssionUsernameCookie, (60 * 10), "/", false, false);
                httpResponse.addCookie(newSesssionUsernameCookie);
                response.setToSuccess();

            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setHeader("Location", LOGIN_PAGE);
                response.setToFailure(CauseOfFailure.INVALID_LOGIN);
            }

            Account updatedAccount = accountRepo.save(targetAccount);
        }
        else {
            //either credentials were wrong or unknown error occurred
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("text/plain");
            httpResponse.setHeader("Location", LOGIN_PAGE);

            if (accountRepo.findByUsername(username) == null) {
                response.setToFailure(CauseOfFailure.USERNAME);
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                response.setToFailure(CauseOfFailure.PASSWORD);
            } else {
                response.setToFailure(CauseOfFailure.UNKNOWN_ERROR);//TODO replace with proper solution later
            }
        }

        return response;
    }

    public AccountServiceResponse logout(SessionRepository sessionRepo, String username, String cookieSessionID,
        HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.LOGOUT);

        if (cookieSessionID.equals("undefined") || cookieSessionID.length() == 0){
            httpResponse.setHeader("Location", LOGIN_PAGE);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setToFailure(CauseOfFailure.SESSION_DNE);
            return response;
        }

        Session targetSesssion = sessionRepo.findBySessionId(AppServiceHelper.hashString(cookieSessionID));

        //if sessionID exists and is valid remove session from DB
        if (targetSesssion != null && targetSesssion.getAccountUsername().equals(username)) {
            sessionRepo.delete(targetSesssion.getId());
            httpResponse.setHeader("Location", LOGIN_PAGE);
            response.setToSuccess();
        } else if (targetSesssion == null) {
            response.setToFailure(CauseOfFailure.SESSION_DNE);
        } else if (!targetSesssion.getAccountUsername().equals(username)) {
            response.setToFailure(CauseOfFailure.INVALID_SESSION);
        } else {
            response.setToFailure(CauseOfFailure.UNKNOWN_ERROR);
        }

        return response;
    }

    public AccountServiceResponse validateUserSession(SessionRepository sessionRepo, HttpServletResponse httpResponse,
        String cookieSessionID) {
        AccountServiceResponse response = new AccountServiceResponse(Service.VALIDATION);

        if (cookieSessionID.equals("undefined") || cookieSessionID.length() == 0){
            httpResponse.setHeader("Location", LOGIN_PAGE);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setToFailure(CauseOfFailure.SESSION_DNE);
            return response;
        }

        Session targetAccount = sessionRepo.findBySessionId(AppServiceHelper.hashString(cookieSessionID));

        //TODO: improve security here after more research
        if (targetAccount != null){
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            response.setToSuccess();
            httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setToFailure(CauseOfFailure.INVALID_SESSION);
            httpResponse.setHeader("Location", LOGIN_PAGE);
        }

        return response;
    }

    /*---==============HELPER FUNCTIONS==============---*/
    /*==================================================*/


}
