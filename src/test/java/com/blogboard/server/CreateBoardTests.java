package com.blogboard.server;

import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.service.BoardService;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.web.BasicResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration
public class CreateBoardTests extends Mockito {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BoardRepository boardRepo;

    @Autowired
    private BoardService boardService;


    String username = "Phoebe";
    String password = "Sm311yCat";
    String email = "chandler@bing.com";
    HttpServletResponse httpResponse;

    @Before
    public void setUpAccount() throws IOException {
        HttpServletResponse createAccountHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount(username,password, email,createAccountHttpResponse);
    }

    @Test
    public void createBoard_happyCase_singleBoard() throws IOException {
        createBoardHelper("Friends", username);
        Account targetAccount = accountRepo.findByUsername(username);
        List<Board> createdBoards = targetAccount.getAdminLevelBoards();
        Board targetBoard = boardRepo.findByNameAndOwner("Friends", targetAccount);

        MatcherAssert.assertThat(createdBoards.size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(createdBoards.get(0), Matchers.notNullValue());
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
    }

    @Test
    public void createBoard_happyCase_twoBoards() throws IOException {
        createBoardHelper("Central Perk Buddies", username);
        createBoardHelper("Smelly Cat Fans", username);

        Account targetAccount = accountRepo.findByUsername(username);
        List<Board> createdBoards = targetAccount.getAdminLevelBoards();
        Board targetBoard1 = boardRepo.findByNameAndOwner("Central Perk Buddies", targetAccount);
        Board targetBoard2 = boardRepo.findByNameAndOwner("Smelly Cat Fans", targetAccount);

        MatcherAssert.assertThat(createdBoards.size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(targetBoard1, Matchers.notNullValue());
        MatcherAssert.assertThat(targetBoard2, Matchers.notNullValue());
    }

    @Test
    public void createBoard_duplicateName() throws IOException {
        createBoardHelper("Friends", username);
        createBoardHelper("Friends", username);
        List<Board> createdBoards = accountRepo.findByUsername(username).getAdminLevelBoards();
        MatcherAssert.assertThat(createdBoards.size(), Matchers.equalTo(1));
    }

    @Test
    public void createBoard_specialCharacters() throws IOException {
        createBoardHelper("Friends", username);
        createBoardHelper("Friénds", username);
        List<Board> createdBoards = accountRepo.findByUsername(username).getAdminLevelBoards();
        MatcherAssert.assertThat(createdBoards.size(), Matchers.equalTo(2));
    }

    @Test
    public void createBoard_caseSensitivity() throws IOException {
        createBoardHelper("Friends", username);
        createBoardHelper("friends", username);
        List<Board> createdBoards = accountRepo.findByUsername(username).getAdminLevelBoards();
        MatcherAssert.assertThat(createdBoards.size(), Matchers.equalTo(2));
    }

    @Test
    public void createBoard_twoDifferentOwners_sameBoardName() throws IOException {
        HttpServletResponse createAccountHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount("Emma", "Th3R3dSw3at3r" , "emma@gellar-green.com", createAccountHttpResponse);
        createBoardHelper("Famjam", "Emma");
        createBoardHelper("Famjam", username);
        List<Board> targetBoards = boardRepo.findByName("Famjam");
        MatcherAssert.assertThat(targetBoards.size(), Matchers.equalTo(2));
    }

    public void createBoardHelper(String name, String username) throws IOException {
        HttpServletResponse createBoardHttpResponse = mock(HttpServletResponse.class);
        boardService.createBoard(true, name, username, createBoardHttpResponse);
    }

}
