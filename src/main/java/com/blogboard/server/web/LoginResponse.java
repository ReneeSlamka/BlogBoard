package com.blogboard.server.web;

public class LoginResponse extends Response {

    private final String loginSuccessURL = "http://localhost:3000/home";
    private final String loginFailureURL = "http://localhost:3000/login";
    private String sessionId;

    public LoginResponse() {
        super();
    }

    private void setLoginSuccessMessage() {
        this.setResponseMessage("Login successful!");
    }

    private void setLoginFailureMessage(String incorrectLoginParameter) {
        //find another way to qualify the response message that doesn't rely on hardcoded strings --> enum?
        if(incorrectLoginParameter.equals("username")) {
            this.setResponseMessage("Sorry, it seems that there is no account with this "
                    + incorrectLoginParameter + ".");
        } else {
            this.setResponseMessage("Sorry, it seems that " + incorrectLoginParameter + " is incorrect.");
        }

    }

    private void setSessionId(String newSessionId) {
        this.sessionId = newSessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setToSuccess(String sessionId) {
        this.setHttpResponseHeader(loginSuccessURL);
        this.setSessionId(sessionId);
        this.setLoginSuccessMessage();
    }

    //TODO: refactor
    @Override
    public void setToSuccess() {}

    @Override
    public void setToFailure(String incorrectLoginParameter) {
        this.setHttpResponseHeader(loginFailureURL);
        this.setLoginFailureMessage(incorrectLoginParameter);
    }




}
