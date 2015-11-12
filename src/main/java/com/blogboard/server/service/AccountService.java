package com.blogboard.server.service;

import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.web.CreateAccountResponse;
import com.blogboard.server.web.LoginResponse;
import com.blogboard.server.web.ValidateUserSessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.servlet.http.Cookie;

@Service
public class AccountService {

    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;

    private static final String UNKNOWN_DATABASE_ERROR = "Unknown database error";
    private static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login attempt";
    private static final String INVALID_SESSION = "Not a valid session";
    private static final String NO_SESSION_FOUND = "No session has been initialized";
    private static final String INVALID_USERNAME = "username";
    private static final String INVALID_PASSWORD = "password";
    private static final String INVALID_EMAIL = "email";
    private static final String UNKNOWN_ERROR = "Unknown error";
    private static final String LOGIN_SUCCESS_URL = "http://localhost:8080/home";
    private static final String LOGIN_FAILURE_URL = "http://localhost:8080/login";
    private static final String CREATE_ACCOUNT_SUCCESS_URL = "http://localhost:8080/account-created";
    private static final String CREATE_ACCOUNT_FAILURE_URL = "http://localhost:8080/login";

    //TODO: how to ensure only one return value for queries that require it?

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }


    public CreateAccountResponse createAccount(String username, String password, String email,
       HttpServletResponse httpResponse) {
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();

        //check if account with that username and/or email already exists
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {

            Account newAccount = new Account(username, hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);

            if (savedAccount == null) {
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                createAccountResponse.setToFailure(UNKNOWN_DATABASE_ERROR);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", CREATE_ACCOUNT_SUCCESS_URL);
                createAccountResponse.setToSuccess();
            }
        } else {
            //either account with same credential(s) already exists or unknown error occurred
            httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            httpResponse.setHeader("Location", CREATE_ACCOUNT_FAILURE_URL);
            if(accountRepo.findByUsername(username) != null) {
                createAccountResponse.setToFailure(INVALID_USERNAME);
            } else if (accountRepo.findByEmail(email) != null) {
                createAccountResponse.setToFailure(INVALID_EMAIL);
            } else {
                //to cover other unknown errors
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                createAccountResponse.setToFailure(UNKNOWN_DATABASE_ERROR); //TODO replace later
            }
        }

        return createAccountResponse;
    }


    public LoginResponse login(String username, String password, HttpServletResponse httpResponse) {
        LoginResponse loginResponse = new LoginResponse();
        Account targetAccount = accountRepo.findByUsername(username);

        //Check if account exists and whether password is correct
        if (targetAccount != null && targetAccount.getPassword().equals(hashString(password))){
            //generate session id and store it to this account in db
            Session previousSession = sessionRepo.findByAccountUsername(username);
            if (previousSession == null) {
                String newSessionId = generateSessionID();
                Session newSession = new Session(username, hashString(newSessionId));
                Session savedSession = sessionRepo.save(newSession);

                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);

                Cookie newSessionIdCookie = new Cookie("sessionID", newSessionId);
                //newSessionIdCookie.setHttpOnly(false);
                //newSessionIdCookie.setSecure(false);
                newSessionIdCookie.setMaxAge(60*10);
                newSessionIdCookie.setPath("/");
                httpResponse.addCookie(newSessionIdCookie);

                Cookie newSesssionUsernameCookie = new Cookie("sessionUsername", username);
                newSesssionUsernameCookie.setMaxAge(60*10);
                newSessionIdCookie.setPath("/");
                httpResponse.addCookie(newSesssionUsernameCookie);

                loginResponse.setToSuccess();
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
                loginResponse.setToFailure(INVALID_LOGIN_ATTEMPT);
            }

            Account updatedAccount = accountRepo.save(targetAccount);
        }
        else {
            //either credentials were wrong or unknown error occurred
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("text/plain");
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);

            if (accountRepo.findByUsername(username) == null) {
                loginResponse.setToFailure(INVALID_USERNAME);
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                loginResponse.setToFailure(INVALID_PASSWORD);
            } else {
                loginResponse.setToFailure(UNKNOWN_ERROR); //TODO replace with proper solution later
            }
        }

        return loginResponse;
    }


    public ValidateUserSessionResponse validateUserSession(HttpServletResponse httpResponse, String cookieSessionID) {
        ValidateUserSessionResponse validationResponse = new ValidateUserSessionResponse();

        if (cookieSessionID.equals("undefined") || cookieSessionID.length() == 0){
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
            validationResponse.setToFailure(NO_SESSION_FOUND);
            return validationResponse;
        }

        Session targetAccount = sessionRepo.findBySessionId(hashString(cookieSessionID));

        //TODO: improve security here after more research
        if (targetAccount != null){
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            validationResponse.setToSuccess();
            httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            validationResponse.setToFailure(INVALID_SESSION);
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
        }

        return validationResponse;
    }


    public Account findOne(Long id) {
        Account account = accountRepo.findOne(id);
        return account;
    }


    public void delete(Long id) {
        accountRepo.delete(id);
    }

    //TODO: is it safe for this method to be part of the account object?
    private String generateSessionID() {
        Random randomNumberGenerator = new Random();
        int randomInt = randomNumberGenerator.nextInt(100);
        String sessionId = "ABC" + String.valueOf(randomInt);

        return sessionId;
    }



    private String hashString(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //TODO: need to somehow return an error to client?
            System.out.println("Hashing function failed to hash password");
        }
        return password;
    }
}
