package com.blogboard.server.service;

import com.blogboard.server.web.CreateAccountResponse;
import com.blogboard.server.web.LoginResponse;
import com.blogboard.server.web.ValidateUserSessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;

import java.util.Random;

@Service
public class AccountService {

    private AccountRepository accountRepo;

    //TODO: how to ensure only one return value for queries that require it?

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }


    public CreateAccountResponse createAccount(String username, String password, String email) {
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();

        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {
            Account newAccount = new Account(username, password, email);
            Account savedAccount = accountRepo.save(newAccount);
            if (savedAccount == null) {
                createAccountResponse.setToFailure("Uknown database error");
            } else {
                createAccountResponse.setToSuccess();
            }
        } else {
            if(accountRepo.findByUsername(username) != null) {
                createAccountResponse.setToFailure("username");
            } else if (accountRepo.findByEmail(email) != null) {
                createAccountResponse.setToFailure("email");
            } else {
                //to cover other unknown errors
                createAccountResponse.setToFailure("UNKNOWN ERROR"); //TODO replace later
            }
        }

        return createAccountResponse;
    }

    public LoginResponse login(String username, String password) {
        LoginResponse loginResponse = new LoginResponse();
        Account targetAccount = accountRepo.findByUsername(username);

        if (targetAccount != null && targetAccount.getPassword().equals(password)){
            //generate session id and store it to this account in db
            String newSessionId;
            //ensure new session id is different from the previous one
            do {
                newSessionId = generateSessionID();
            } while(generateSessionID().equals(targetAccount.getSessionId()));

            targetAccount.setSessionId(newSessionId);
            Account updatedAccount = accountRepo.save(targetAccount);
            loginResponse.setToSuccess(newSessionId);
        }
        else {
            if (accountRepo.findByUsername(username) == null) {
                loginResponse.setToFailure("username");
            } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
                loginResponse.setToFailure("password");
            } else {
                loginResponse.setToFailure("UNKNOWN ERROR"); //TODO replace with proper solution later
            }
        }

        return loginResponse;
    }

    public ValidateUserSessionResponse validateUserSession(String sessionId) {
        ValidateUserSessionResponse validationResponse = new ValidateUserSessionResponse();

        if (sessionId.length() == 0){
            validationResponse.setToFailure("No session has been initialized");
            return validationResponse;
        }

        Account targetAccount = accountRepo.findBySessionId(sessionId);

        //TODO: improve security here after more research
        if (targetAccount != null){
            validationResponse.setToSuccess();
        } else {
            validationResponse.setToFailure("Not a valid session");
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

}
