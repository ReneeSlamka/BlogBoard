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

import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class AccountService {

    private AccountRepository accountRepo;
    private SessionRepository sessionRepo;

    public static final String UNKNOWN_DATABASE_ERROR = "Uknown database error";
    public static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login atttempt";
    public static final String INVALID_SESSION = "Not a valid session";
    public static final String NO_SESSION_FOUND = "No session has been initialized";
    public static final String INVALID_USERNAME = "username";
    public static final String INVALID_PASSWORD = "password";
    public static final String INVALID_EMAIL = "email";
    public static final String UNKNOWN_ERROR = "Unknown error";

    //TODO: how to ensure only one return value for queries that require it?

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired
    public void setSessionRepository(SessionRepository sessionRepository) {
        this.sessionRepo = sessionRepository;
    }


    public CreateAccountResponse createAccount(String username, String password, String email) {
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();

        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {

            String hashedPassword = password;
            try {
                hashedPassword = hashPassword(password);
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Hashing function failed to hash password");
            }

            Account newAccount = new Account(username, hashedPassword, email);
            Account savedAccount = accountRepo.save(newAccount);

            if (savedAccount == null) {
                createAccountResponse.setToFailure(UNKNOWN_DATABASE_ERROR);
            } else {
                createAccountResponse.setToSuccess();
            }
        } else {
            if(accountRepo.findByUsername(username) != null) {
                createAccountResponse.setToFailure(INVALID_USERNAME);
            } else if (accountRepo.findByEmail(email) != null) {
                createAccountResponse.setToFailure(INVALID_EMAIL);
            } else {
                //to cover other unknown errors
                createAccountResponse.setToFailure(UNKNOWN_DATABASE_ERROR); //TODO replace later
            }
        }

        return createAccountResponse;
    }

    public LoginResponse login(String username, String password) {
        LoginResponse loginResponse = new LoginResponse();
        Account targetAccount = accountRepo.findByUsername(username);

        String hashedPassword = password;
        try {
            hashedPassword = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hashing function failed to hash password");
        }

        if (targetAccount != null && targetAccount.getPassword().equals(hashedPassword)){
            //generate session id and store it to this account in db
            Session previousSession = sessionRepo.findByAccountUsername(username);
            if (previousSession == null) {
                String newSessionId = generateSessionID();
                Session newSession = new Session(username, newSessionId);
                loginResponse.setToSuccess(newSessionId);
            } else {
                loginResponse.setToFailure(INVALID_LOGIN_ATTEMPT);
            }

            Account updatedAccount = accountRepo.save(targetAccount);
            loginResponse.setToSuccess("");//newSessionId);
        }
        else {
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

    public ValidateUserSessionResponse validateUserSession(String sessionId) {
        ValidateUserSessionResponse validationResponse = new ValidateUserSessionResponse();

        if (sessionId.length() == 0){
            validationResponse.setToFailure(NO_SESSION_FOUND);
            return validationResponse;
        }

        Session targetAccount = sessionRepo.findBySessionId(sessionId);

        //TODO: improve security here after more research
        if (targetAccount != null){
            validationResponse.setToSuccess();
        } else {
            validationResponse.setToFailure(INVALID_SESSION);
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

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
