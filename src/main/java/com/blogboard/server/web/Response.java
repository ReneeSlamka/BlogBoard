package com.blogboard.server.web;


public abstract class Response {

    public String httpResponseHeader;
    private String responseMessage;

    public Response() {
        this.httpResponseHeader = "";
        this.responseMessage = "";
    }

    public String getHttpResponseHeader() {
        return this.httpResponseHeader;
    }

    protected void setHttpResponseHeader(String newHeader) {
        this.httpResponseHeader = newHeader;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public void setResponseMessage(String newResponseText) {
        this.responseMessage = newResponseText;
    }

    abstract public void setToSuccess();

    abstract public void setToFailure(String reasonForFailure);



}
