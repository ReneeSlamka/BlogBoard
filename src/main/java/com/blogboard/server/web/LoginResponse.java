package com.blogboard.server.web;

public class LoginResponse extends Response {

    private String loginSuccessURL = "http://localhost:3000/home";
    private String loginFailureURL = "http://localhost:3000/login";

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

    @Override
    public void setToSuccess() {
        this.setHttpResponseHeader(loginSuccessURL);
        this.setLoginSuccessMessage();
    }

    @Override
    public void setToFailure(String incorrectLoginParameter) {
        this.setHttpResponseHeader(loginFailureURL);
        this.setLoginFailureMessage(incorrectLoginParameter);
    }




}
