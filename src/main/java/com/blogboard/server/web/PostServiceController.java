package com.blogboard.server.web;

import com.blogboard.server.service.AuthenticationService;
import com.blogboard.server.service.PostService;
import com.blogboard.server.web.ServiceResponses.AddPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class PostServiceController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PostService postService;

    private static final Logger logger = Logger.getLogger(AccountServicesController.class.getName());

    /*
    *========== Add Post ==========
    */

    @RequestMapping(value = "/boards/{boardId}/posts", method = RequestMethod.POST)
    public
    @ResponseBody
    AddPostResponse addPost(
            @PathVariable Long boardId,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "textBody", required = true) String textBody,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return postService.addPost(sessionValid, boardId, sessionUsername, title, textBody, httpResponse);
    }


    /*
    *========== Edit Post ==========
    */

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.POST)
    public
    @ResponseBody
    BasicResponse editPost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @RequestParam(value = "title", required = true) String editedTitle,
            @RequestParam(value = "textBody", required = true) String editedTextBody,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return postService.editPost(sessionValid, boardId, postId, editedTitle, editedTextBody, httpResponse);
    }


    /*
    *========== Delete Post ==========
    */

    @RequestMapping(value = "/boards/{boardId}/posts/{postId}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    BasicResponse deletePost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @CookieValue(value = "sessionUsername", defaultValue = "", required = false) String sessionUsername,
            @CookieValue(value = "sessionID", defaultValue = "", required = false) String sessionId,
            HttpServletResponse httpResponse) throws IOException {

        boolean sessionValid = authenticationService.validateSession(sessionId, sessionUsername, httpResponse);
        return postService.deletePost(sessionValid, boardId, postId, httpResponse);
    }
}