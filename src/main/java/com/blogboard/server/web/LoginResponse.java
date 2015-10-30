package com.blogboard.server.web;

/**
 * Created by renee on 2015-10-29.
 */
public class LoginResponse {
    private String httpResponseHeader = null;

    private boolean sessionValid = false;

    public LoginResponse() { super(); }

    public void setHttpResponseHeader(String newHeader) {
        this.httpResponseHeader = newHeader;
    }

    public String getHttpResponseHeader() {
        return this.httpResponseHeader;
    }

    public boolean getSessionValidity() {
        return sessionValid;
    }

    public void setSessionValidity(boolean sessionValid) {
        this.sessionValid = sessionValid;
    }
}
