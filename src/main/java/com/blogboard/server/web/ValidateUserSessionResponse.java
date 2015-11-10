package com.blogboard.server.web;

public class ValidateUserSessionResponse extends Response {

    public ValidateUserSessionResponse () { super(); }

    @Override
    public void setToSuccess() {

    }

    @Override
    public void setToFailure(String failureMessage) {
        this.setResponseMessage(failureMessage);
    }
}
