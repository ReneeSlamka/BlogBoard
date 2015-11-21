package com.blogboard.server.web;

import com.blogboard.server.service.AccountService.Service;


public class AccountServiceResponse extends Response {

    private static final String ACCOUNT_CREATION_SUCCESS = "Congrats, your account has successfully been " +
            "created! You can now login and start blogging.";
    private static final String LOGIN_SUCCESS = "Login successful!";
    private static final String LOGOUT_SUCCESS = "Logout successful";
    private static final String SESSION_VALID = "Valid login session created";

    private Service serviceType;

    public AccountServiceResponse (Service serviceType) {
        super();
        this.serviceType = serviceType;
    }

    @Override
    public void setToSuccess() {
        switch (serviceType) {
            case ACCOUNT_CREATION:
                this.setMessage(ACCOUNT_CREATION_SUCCESS);
                break;

            case LOGIN:
                this.setMessage(LOGIN_SUCCESS);
                break;

            case VALIDATION:
                this.setMessage(SESSION_VALID);
                break;

            case LOGOUT:
                this.setMessage(LOGOUT_SUCCESS);
                break;

            default:
                this.setMessage("Undefined success");
                break;
        }
    }
}