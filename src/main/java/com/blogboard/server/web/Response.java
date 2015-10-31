package com.blogboard.server.web;


public class Response {

    public String httpResponseHeader;

    private String responseText;

    public Response() {
        this.httpResponseHeader = "";
        this.responseText = "";
    }

    public String getHttpResponseHeader() {
        return this.httpResponseHeader;
    }

    protected void setHttpResponseHeader(String newHeader) {
        this.httpResponseHeader = newHeader;
    }

    public String getResponseText() {
        return this.responseText;
    }

    public void setResponseText(String newResponseText) {
        this.responseText = newResponseText;
    }

}
