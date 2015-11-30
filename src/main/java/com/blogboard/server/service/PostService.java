package com.blogboard.server.service;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Post;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.data.repository.PostRepository;
import com.blogboard.server.web.BasicResponse;
import com.blogboard.server.web.ServiceResponses.AddPostResponse;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class PostService {

    private static final String POST_CREATED = "Post successfully added";
    private static final String DUPLICATE_POST = "This post has already been added to this board";

    /*
   * Method Name: Add Post
   * Inputs: boardId, authorUsername, title, textContent
   * Return: AddPostResponse
   * Purpose: adds a post object to the specified board
    */
    public AddPostResponse addPost(AccountRepository accountRepo, BoardRepository boardRepo, PostRepository postRepo,
                                   Long boardId, String authorUsername, String title, String textContent,
                                   HttpServletResponse httpResponse) throws IOException {

        AddPostResponse response = new AddPostResponse();
        Account targetAccount = accountRepo.findByUsername(authorUsername);
        Board targetBoard = boardRepo.findOne(boardId);
        String timeStamp = AppServiceHelper.createTimeStamp();
        Post newPost = new Post(title, targetAccount, timeStamp);
        newPost.setTextContent(textContent);

        if (targetBoard != null) {
            if (targetBoard.addPost(newPost)) {
                httpResponse.setStatus(HttpServletResponse.SC_CREATED);
                response.setMessage(POST_CREATED);
            } else {
                //would this ever happen though?
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, DUPLICATE_POST);
            }
        } else {
            //todo: find a way to deal with this error in board class to remove unecessary duplicate code
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BoardService.BOARD_NOT_FOUND);
        }

        return response;
    }


    /*
   * Method Name: Edit Post
   * Inputs: BoardId, PostId, EditedTitle, EditedText
   * Return: BasicResponse
   * Purpose:
    */

    public BasicResponse editPost(AccountRepository accountRepo, BoardRepository boardRepo, PostRepository postRepo,
                                  Long boardId, Long postId, String editedTitle, String editedText) {

        BasicResponse response = new BasicResponse();

        return response;
    }



    /*
   * Method Name: Delete Post
   * Inputs: BoardId, PostId
   * Return:
   * Purpose:
    */

    public BasicResponse deletePost(AccountRepository accountRepo, BoardRepository boardRepo, PostRepository postRepo,
                                  Long boardId, Long postId) {

        BasicResponse response = new BasicResponse();

        return response;
    }
}
