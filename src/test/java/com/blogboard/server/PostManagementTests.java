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
public class PostManagementTests extends Mockito{

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

    @Test
    public void deletePost_postDNE() throws IOException {
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        postService.deletePost(true, targetBoard.getId(), 100L, httpResponse);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
    }

    @Test
    public void deletePost_wrongBoardId() throws IOException {
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        Post targetPost = targetBoard.getPosts().get(0);
        MatcherAssert.assertThat(targetBoard.getId(), Matchers.not(100L));
        postService.deletePost(true, 100L, targetPost.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
    }



    @Test
    public void editPost_happyCase() throws IOException {
        String newTitle = "New Title";
        String newText = "Changed post body";

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);

        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        Post targetPost = targetBoard.getPosts().get(0);
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(postBody));
        postService.editPost(true, targetBoard.getId(), targetPost.getId(), newTitle, newText, httpResponse);

        MatcherAssert.assertThat(targetPost.getTitle(), Matchers.equalTo(newTitle));
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(newText));
    }

    @Test
    public void editPost_wrongPostId() throws IOException {
        String newTitle = "New Title";
        String newText = "Changed post body";

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);

        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        Post targetPost = targetBoard.getPosts().get(0);
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(postBody));
        MatcherAssert.assertThat(targetPost.getId(), Matchers.not(100L));
        postService.editPost(true, targetBoard.getId(), 100L, newTitle, newText, httpResponse);

        MatcherAssert.assertThat(targetPost.getTitle(), Matchers.equalTo(title));
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(postBody));
    }


    @Test
    public void editPost_wrongBoardId() throws IOException {
        String newTitle = "New Title";
        String newText = "Changed post body";

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        addPostHelper(targetBoard, username, title, postBody);

        MatcherAssert.assertThat(targetBoard.getPosts().size(), Matchers.equalTo(1));
        Post targetPost = targetBoard.getPosts().get(0);
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(postBody));
        MatcherAssert.assertThat(targetBoard.getId(), Matchers.not(100L));
        postService.editPost(true, 100L, targetPost.getId(), newTitle, newText, httpResponse);

        MatcherAssert.assertThat(targetPost.getTitle(), Matchers.equalTo(title));
        MatcherAssert.assertThat(targetPost.getTextContent(), Matchers.equalTo(postBody));
    }
}
