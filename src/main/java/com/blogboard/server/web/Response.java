package com.blogboard.server.web;
import com.blogboard.server.service.AccountService;


public abstract class Response {

    private String responseMessage;

    public Response() {
        this.responseMessage = "";
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String newResponseText) {
        this.responseMessage = newResponseText;
    }

    abstract public void setToSuccess();

    abstract public void setToFailure(AccountService.CauseOfFailure reasonForFailure);

}
