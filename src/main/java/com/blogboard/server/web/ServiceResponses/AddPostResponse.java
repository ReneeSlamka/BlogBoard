package com.blogboard.server.web.ServiceResponses;

import com.blogboard.server.web.BasicResponse;

public class AddPostResponse extends BasicResponse {


    private String authorUsername;
    private String timeStamp;

    public AddPostResponse() {
        super();
    }

    public String getAuthorUsername() { return authorUsername; }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
