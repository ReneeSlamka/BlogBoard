package com.blogboard.server;

import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.data.repository.PostRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.service.BoardService;
import com.blogboard.server.service.PostService;
import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Post;
import com.blogboard.server.web.BasicResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration
public class CreatePostTests extends Mockito{

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BoardRepository boardRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private PostService postService;

    String username = "Monica";
    String password = "IKN0W!";
    String email = "monica@gellar.com";
    String boardName = "Team Monica";
    String title = "My Cleaning Store Haul";
    String postBody = "I got Mr.Clean's new Magic Eraser! I KNOW!";

    @Before
    public void setUpAccountBoard() throws IOException {
        HttpServletResponse createAccountHttpResponse = mock(HttpServletResponse.class);
        HttpServletResponse createBoardHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount(username, password, email, createAccountHttpResponse);
        boardService.createBoard(true, boardName, username, createAccountHttpResponse);
    }

    @Test
    public void createPost_happyCase_singlePost() throws IOException {
        HttpServletResponse addPostHttpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        postService.addPost(true, targetBoard.getId(), username, title, postBody, addPostHttpResponse);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(targetBoard.getPosts().get(0).getTitle(), Matchers.equalTo(title));
    }

    @Test
    public void createPost_happyCase_twoPosts() throws IOException {
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);
        addPostHelper(targetBoard, username, "Rossatron", "Can this be a thing?");
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(2));
    }

    @Test
    public void createPost_happyCase_twoPosts_sameName() throws IOException {
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);
        addPostHelper(targetBoard, username, title, postBody);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(2));
    }

    public void addPostHelper(Board targetBoard, String author, String title, String postBody) throws IOException {
        HttpServletResponse addPostResponse = mock(HttpServletResponse.class);
        postService.addPost(true, targetBoard.getId(), author, title, postBody, addPostResponse);
    }

    @Test
    public void deletePost_happyCase_singlePost() throws IOException {
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        Post targetPost = targetBoard.getPosts().get(0);
        postService.deletePost(true, targetBoard.getId(), targetPost.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(0));
    }
}
