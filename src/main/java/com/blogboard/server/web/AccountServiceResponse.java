package com.blogboard.server.web;

import com.blogboard.server.service.AccountService.Service;
import com.blogboard.server.service.AccountService.CauseOfFailure;


public class AccountServiceResponse extends Response {

    private static final String ACCOUNT_CREATION_SUCCESS = "Congrats, your account has successfully been " +
            "created! You can now login and start blogging.";
    private String ACCOUNT_CREATION_FAILURE = "Sorry, it seems there is already an account with that " +
            "credential" + ". Please try making another account with a different "
            + "credential" + ".";

    private static final String LOGIN_SUCCESS = "Login successful!";
    private String LOGIN_FAILURE_USERNAME = "Sorry, it seems that there is no account with that username.";
    private String LOGIN_FAILURE_PASSWORD = "Sorry, it seems that password is incorrect.";
    private static final String LOGIN_INVALID = "Session already in place, invalid login attempt";
    private static final String INVALID_LOGIN_ATTEMPT = "Session already in place, invalid login attempt";
    private static final String SESSION_VALID = "";
    private static final String SESSION_INVALID = "";
    private static final String NO_SESSION_FOUND = "No session has been initialized";
    private static final String INVALID_SESSION = "Not a valid session";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";

    private Service serviceType;

    public AccountServiceResponse (Service serviceType) {
        super();
        this.serviceType = serviceType;
    }

    @Override
    public void setToSuccess() {
        switch (serviceType) {
            case ACCOUNT_CREATION:
                this.setResponseMessage(ACCOUNT_CREATION_SUCCESS);
                break;

            case LOGIN:
                this.setResponseMessage(LOGIN_SUCCESS);
                break;

            case VALIDATION:
                this.setResponseMessage(SESSION_VALID);
                break;

            default:
                this.setResponseMessage("Undefined success");
                break;
        }
    }

    public void setToFailure(CauseOfFailure causeOfFailure) {
        this.setResponseMessage(UNKNOWN_ERROR);
        switch (serviceType) {
            case ACCOUNT_CREATION:
                if (causeOfFailure.equals(CauseOfFailure.USERNAME)) {
                    this.setResponseMessage(ACCOUNT_CREATION_FAILURE.replace("credential", "username"));
                } else if (causeOfFailure.equals(CauseOfFailure.EMAIL)) {
                    this.setResponseMessage(ACCOUNT_CREATION_FAILURE.replace("credential", "email"));
                }
                break;

            case LOGIN:
                if (causeOfFailure.equals(CauseOfFailure.USERNAME)){
                    this.setResponseMessage(LOGIN_FAILURE_USERNAME);
                } else if (causeOfFailure.equals(CauseOfFailure.PASSWORD)) {
                    this.setResponseMessage(LOGIN_FAILURE_PASSWORD);
                } else if (causeOfFailure.equals(CauseOfFailure.INVALID_LOGIN)) {
                    this.setResponseMessage(INVALID_LOGIN_ATTEMPT);
                }
                break;

            case VALIDATION:
                if (causeOfFailure.equals(CauseOfFailure.SESSION_DNE)) {
                    this.setResponseMessage(NO_SESSION_FOUND);
                } else if (causeOfFailure.equals(INVALID_SESSION)) {
                    this.setResponseMessage(INVALID_SESSION);
                }
                break;
        }
    }
}