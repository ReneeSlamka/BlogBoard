package com.blogboard.server;


import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Board;
import com.blogboard.server.service.BoardService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration
public class BoardMemberTests extends Mockito {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BoardRepository boardRepo;

    @Autowired
    private BoardService boardService;


    String username = "Ben";
    String password = "HolidayArmadill0";
    String email = "ben@gellar.com";
    String boardName = "Central Perk";
    HttpServletResponse httpResponse;

    @Before
    public void setupResponse() throws IOException {
        HttpServletResponse httpResponse1 = mock(HttpServletResponse.class);
        HttpServletResponse httpResponse2 = mock(HttpServletResponse.class);
        accountService.createAccount(username, password, email, httpResponse1);
        Account targetAccount = accountRepo.findByUsername(username);
        boardService.createBoard(true, boardName, username, httpResponse2);
        httpResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void addMember_happyCase_singleAccount() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        boardService.addMember(true, "Regina", targetBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(2));
    }

    @Test
    public void addMember_happyCase_twoAccounts() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        createAccountHelper("Richard", "MustacheK1ng", "rich@optometrist.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        boardService.addMember(true, "Regina", targetBoard.getId(), httpResponse);
        boardService.addMember(true, "Richard", targetBoard.getId(), secondHttpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(3));
    }

    @Test
    public void addMember_addSameAccountTwice() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        Account targetAccount1 = accountRepo.findByUsername("Regina");
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        boardService.addMember(true, "Regina", targetBoard.getId(), httpResponse);
        boardService.addMember(true, "Regina", targetBoard.getId(), secondHttpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(2));
    }

    @Test
    public void addMember_accountDNE() throws IOException {
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));
        boardService.addMember(true, "Gunther", targetBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));
    }

    @Test
    public void addMember_incorrectBoardId() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        Account targetAccount1 = accountRepo.findByUsername("Regina");
        MatcherAssert.assertThat(targetBoard.getId(), Matchers.not(100L));
        boardService.addMember(true, "Regina", 100L, httpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

    }

    public void createAccountHelper(String name, String password, String email) throws IOException {
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        accountService.createAccount(name, password, email, httpResponse);
    }
}

