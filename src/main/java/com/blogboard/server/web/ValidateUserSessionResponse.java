package com.blogboard.server.web;

public class ValidateUserSessionResponse extends Response {

    private String sessionValidationSuccess = "http://localhost:3000/home";
    private String sessionValidationFail = "http://localhost:3000/login";

    public ValidateUserSessionResponse () { super(); }

    @Override
    public void setToSuccess() {
        this.setHttpResponseHeader(sessionValidationSuccess);
    }

    @Override
    public void setToFailure(String failureMessage) {
        this.setHttpResponseHeader(sessionValidationFail);
        this.setResponseMessage(failureMessage);
    }
}
