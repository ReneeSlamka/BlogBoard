package com.blogboard.server.web;

public abstract class BasicAPIResponse {

    private String message;

    public BasicAPIResponse() {
        this.message = "";
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String newResponseText) {
        this.message = newResponseText;
    }

}
