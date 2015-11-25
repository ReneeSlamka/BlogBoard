package com.blogboard.server.web.ServiceResponses;


import com.blogboard.server.web.BasicResponse;

public class AddMemberResponse extends BasicResponse {

    private String username;
    private String url;

    public AddMemberResponse() { super(); }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
