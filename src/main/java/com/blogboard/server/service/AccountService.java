package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.*;
import com.blogboard.server.data.repository.*;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.servlet.http.Cookie;

@Service
public class AccountService {

    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;
    private BoardRepository boardRepo;

    private static final String LOGIN_SUCCESS_URL = "http://localhost:8080/home";
    private static final String LOGIN_FAILURE_URL = "http://localhost:8080/login";
    private static final String CREATE_ACCOUNT_SUCCESS_URL = "http://localhost:8080/account-created";
    private static final String CREATE_ACCOUNT_FAILURE_URL = "http://localhost:8080/login";
    private static final String USER_HOME_URL = "http://localhost:8080/home";

    public static enum CauseOfFailure {
        USERNAME, PASSWORD, EMAIL, SESSION, DATABASE_ERROR, INVALID_LOGIN, SESSION_DNE,
        INVALID_SESSION, UNKNOWN_ERROR
    }

    public static enum Service {
        ACCOUNT_CREATION, LOGIN, VALIDATION, BOARD_CREATION, ADD_MEMBER, REMOVE_MEMBER
    }


    //TODO: how to ensure only one return value for queries that require it?

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }

    @Autowired
    public void setSessionRepository(BoardRepository boardRepository) {
        this.boardRepo = boardRepository;
    }


    public AccountServiceResponse createAccount(String username, String password, String email,
       HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.ACCOUNT_CREATION);

        //check if account with that username and/or email already exists
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {

            Account newAccount = new Account(username, hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);

            if (savedAccount == null) {
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setToFailure(CauseOfFailure.DATABASE_ERROR);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", CREATE_ACCOUNT_SUCCESS_URL);
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


    public AccountServiceResponse login(String username, String password, HttpServletResponse httpResponse) {
        AccountServiceResponse response = new AccountServiceResponse(Service.LOGIN);
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
                configureCookie(newSessionIdCookie, (60*10), "/", false, false);
                httpResponse.addCookie(newSessionIdCookie);

                Cookie newSesssionUsernameCookie = new Cookie("sessionUsername", username);
                configureCookie(newSesssionUsernameCookie, (60*10), "/", false, false);
                httpResponse.addCookie(newSesssionUsernameCookie);
                response.setToSuccess();

            } else {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
                response.setToFailure(CauseOfFailure.INVALID_LOGIN);
            }

            Account updatedAccount = accountRepo.save(targetAccount);
        }
        else {
            //either credentials were wrong or unknown error occurred
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("text/plain");
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);

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


    public AccountServiceResponse validateUserSession(HttpServletResponse httpResponse, String cookieSessionID) {
        AccountServiceResponse response = new AccountServiceResponse(Service.VALIDATION);

        if (cookieSessionID.equals("undefined") || cookieSessionID.length() == 0){
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
            response.setToFailure(CauseOfFailure.SESSION_DNE);
            return response;
        }

        Session targetAccount = sessionRepo.findBySessionId(hashString(cookieSessionID));

        //TODO: improve security here after more research
        if (targetAccount != null){
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            response.setToSuccess();
            httpResponse.setHeader("Location", LOGIN_SUCCESS_URL);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setToFailure(CauseOfFailure.INVALID_SESSION);
            httpResponse.setHeader("Location", LOGIN_FAILURE_URL);
        }

        return response;
    }

    public Account findOne(Long id) {
        Account account = accountRepo.findOne(id);
        return account;
    }


    public void delete(Long id) {
        accountRepo.delete(id);
    }



    /*---============== BOARD FUNCTIONS ==============---*/
    /*===================================================*/

    public CreateBoardResponse createBoard(String name, String ownerUsername,
       HttpServletResponse httpResponse) {

        CreateBoardResponse createBoardResponse = new CreateBoardResponse();

        //check if board with that owner AND name already exists
        if (boardRepo.findByNameAndOwnerUsername(name, ownerUsername) == null) {
            //create board and save in board repo
            Board newBoard = new Board(name, ownerUsername);
            Board savedBoard = boardRepo.save(newBoard);

            Cookie userBoardsCookie = new Cookie("userBoards", name);
            configureCookie(userBoardsCookie, (60*10), "/", false, false);//change path to home, etc later?
            httpResponse.addCookie(userBoardsCookie);
            httpResponse.setStatus(HttpServletResponse.SC_OK);

            //configure response
            createBoardResponse.setToSuccess();
        } else {
            createBoardResponse.setToFailure("name");
            //return error messages saying board already exists
            httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        httpResponse.setHeader("Location", USER_HOME_URL);
        return createBoardResponse;
    }



    /*---==============HELPER FUNCTIONS==============---*/
    /*==================================================*/

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

    private void configureCookie(Cookie cookie, int maxAge, String path, boolean httpOnly, boolean isSecure) {
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(isSecure);
    }

    private void configureHttpServlet(HttpServletResponse servletResponse, int status, String locationHeaderURL, Cookie cookie) {
        servletResponse.setStatus(status);
        servletResponse.setHeader("Location", locationHeaderURL);
        if (cookie != null) {
            servletResponse.addCookie(cookie);
        }
    }
}
