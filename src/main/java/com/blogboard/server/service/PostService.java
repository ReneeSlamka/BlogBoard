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
    private static final String POST_DELETED = "Post successfully deleted";
    private static final String POST_NOT_FOUND = "Post not found";
    private static final String POST_NOT_DELETED = "Post not found or not part of this board";
    private static final String POST_EDITED = "Changes to post successfully saved";

    /*
   * Method Name: Add Post
   * Inputs: boardId, authorUsername, title, textContent
   * Return: AddPostResponse
   * Purpose: adds a post object to the specified board
    */
    public AddPostResponse addPost(AccountRepository accountRepo, BoardRepository boardRepo, PostRepository postRepo,
                                   boolean sessionValid, Long boardId, String authorUsername, String title,
                                   String textContent, HttpServletResponse httpResponse) throws IOException {

        AddPostResponse response = new AddPostResponse();
        if (!sessionValid) { return response; }
        Board targetBoard = boardRepo.findOne(boardId);

        if (targetBoard != null) {
            Account targetAccount = accountRepo.findByUsername(authorUsername);
            String timeStamp = AppServiceHelper.createTimeStamp();
            Post newPost = new Post(title, targetAccount, timeStamp);
            newPost.setTextContent(textContent);
            Post savedPost = postRepo.save(newPost);

            if (targetBoard.addPost(savedPost)) {
                Board savedBoard = boardRepo.save(targetBoard);
                httpResponse.setStatus(HttpServletResponse.SC_CREATED);
                response.setAuthorUsername(authorUsername);
                response.setTimeStamp(timeStamp);
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
                                  boolean sessionValid, Long boardId, Long postId, String editedTitle, String editedText,
                                  HttpServletResponse httpResponse) throws IOException{

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }

        Board targetBoard = boardRepo.findOne(boardId);
        Post targetPost = postRepo.findOne(postId);

        if (targetBoard != null) {
            if (targetPost != null) {
                targetPost.setTitle(editedTitle);
                targetPost.setTextContent(editedText);
                Post editedPost = postRepo.save(targetPost);
                //Todo: is resaving really necessary?
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                response.setMessage(POST_EDITED);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, POST_NOT_FOUND);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BoardService.BOARD_NOT_FOUND);
        }

        return response;
    }


    /*
   * Method Name: Delete Post
   * Inputs: BoardId, PostId
   * Return:
   * Purpose:
    */

    public BasicResponse deletePost(AccountRepository accountRepo, BoardRepository boardRepo, PostRepository postRepo,
                                  boolean sessionValid,Long boardId, Long postId, HttpServletResponse httpResponse)
                                  throws IOException {

        BasicResponse response = new BasicResponse();
        if (!sessionValid) { return response; }

        Board targetBoard = boardRepo.findOne(boardId);
        Post targetPost = postRepo.findOne(postId);

        if (targetBoard != null) {
            if (targetPost != null) {
                if (targetBoard.deletePost(targetPost)) {
                    //Todo: save board? didn't work...
                    Board savedBoard = boardRepo.save(targetBoard);
                    //Todo: rethink this ordering?
                    postRepo.delete(postId);
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    response.setMessage(POST_DELETED);
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, POST_NOT_DELETED);
                }
            } else {
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, POST_NOT_FOUND);
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, BoardService.BOARD_NOT_FOUND);
        }

        return response;
    }
}
