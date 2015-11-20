package com.blogboard.server.web;
import com.blogboard.server.service.AccountService;


public abstract class Response {

    private String message;

    public Response() {
        this.message = "";
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String newResponseText) {
        this.message = newResponseText;
    }

    abstract public void setToSuccess();

}
