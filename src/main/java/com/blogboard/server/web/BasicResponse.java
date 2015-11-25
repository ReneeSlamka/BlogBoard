package com.blogboard.server.web;

public class BasicResponse {

    private String message;

    public BasicResponse() {
        this.message = "";
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
