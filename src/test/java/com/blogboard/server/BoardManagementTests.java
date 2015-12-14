package com.blogboard.server;


import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.BoardRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Board;
import com.blogboard.server.service.BoardService;
import com.blogboard.server.web.BasicResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
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
import javax.validation.constraints.AssertTrue;
import java.io.IOException;



@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration
public class BoardManagementTests extends Mockito {

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
    public void setupAccountBoardPair() throws IOException {
        HttpServletResponse httpResponse1 = mock(HttpServletResponse.class);
        HttpServletResponse httpResponse2 = mock(HttpServletResponse.class);
        //create an account and its own board
        accountService.createAccount(username, password, email, httpResponse1);
        boardService.createBoard(true, boardName, username, httpResponse2);
        httpResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void editBoard_happyCase() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        //change name of user's board to another unique name
        boardService.editBoard(true, targetBoard.getId(), username, "Grade 2 Classmates", httpResponse);
        MatcherAssert.assertThat(targetBoard.getName(), Matchers.equalTo("Grade 2 Classmates"));
    }


    @Test
    public void editBoard_sameNameAsBefore() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        //change name of user's board to the same name it had before --> should ignore this request
        BasicResponse response = boardService.editBoard(true, targetBoard.getId(), username, boardName, httpResponse);
        MatcherAssert.assertThat(targetBoard.getName(), Matchers.equalTo(boardName));
        MatcherAssert.assertThat(response.getMessage(), Matchers.nullValue()); //implies an error
    }

    @Test
    public void editBoard_sameNameAsOtherAdminLevelBoard() throws IOException {
        createBoardHelper(username, "The Fishbowl");
        Account targetAccount = accountRepo.findByUsername(username);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        //change name of user's board to the same name it had before --> should ignore this request
        boardService.editBoard(true, targetBoard.getId(), username, "The Fishbowl", httpResponse);
        MatcherAssert.assertThat(targetBoard.getName(), Matchers.equalTo(boardName));
    }


    @Test
    public void editBoard_sameNameAsOtherAccessibleBoard() throws IOException {
        //create another account with its own board and add fist account to it as a member
        createAccountHelper("FunBobby", "FunStuff1996", "fun@bobby.com");
        Account targetPrimaryAccount = accountRepo.findByEmail(email);
        Account targetSecondaryAccount = accountRepo.findByEmail("fun@bobby.com");
        createBoardHelper("FunBobby", "Fun Stuff Group");
        Board targetSecondaryBoard = boardRepo.findByNameAndOwner("Fun Stuff Group", targetSecondaryAccount);
        MatcherAssert.assertThat(targetSecondaryBoard, Matchers.notNullValue());

        //primary user should now have a board in their accessible board list
        boardService.addMember(true, username, targetSecondaryBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetPrimaryAccount.getAccessibleBoards().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(targetPrimaryAccount.getAccessibleBoards()
                .contains(targetSecondaryBoard), Matchers.is(true));
        Board targetPrimaryBoard = boardRepo.findByNameAndOwner(boardName,targetPrimaryAccount);

        //change name of primary user's board to match secondary user's board
        HttpServletResponse editBoardHttpResponse = mock(HttpServletResponse.class);
        boardService.editBoard(true, targetPrimaryBoard.getId(), username, "Fun Stuff Group", editBoardHttpResponse);

        //should still work because the other board is only a part of the primary user's accessible board list
        //which allows for duplicates
        MatcherAssert.assertThat(targetPrimaryBoard.getName(), Matchers.equalTo("Fun Stuff Group"));
    }

    @Test
    public void editBoard_specialCharacters() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        //change name of user's board to another unique name
        boardService.editBoard(true, targetBoard.getId(), username, "Céntral Perk", httpResponse);
        MatcherAssert.assertThat(targetBoard.getName(), Matchers.equalTo("Céntral Perk"));
    }

    @Test
    public void editBoard_boardDNE_incorrectBoardId() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        //change name of user's board to another unique name
        boardService.editBoard(true, 100L, username, "Cowboy Dads", httpResponse);
        MatcherAssert.assertThat(targetBoard.getName(), Matchers.equalTo(boardName));
    }

    @Test
    public void deleteBoard_happyCase() throws IOException {
        createAccountHelper("Joey", "p1zza", "joey@tribiani.com");
        Account targetAccount = accountRepo.findByEmail(email);
        Account secondaryAccount = accountRepo.findByEmail("joey@tribiani.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        boardService.addMember(true, "Joey", targetBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        MatcherAssert.assertThat(targetAccount.getAdminLevelBoards().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(secondaryAccount.getAccessibleBoards().size(), Matchers.equalTo(1));

        HttpServletResponse deleteBoardHttpResponse = mock(HttpServletResponse.class);
        boardService.deleteBoard(true, targetBoard.getId(),username, deleteBoardHttpResponse);
        MatcherAssert.assertThat(targetAccount.getAdminLevelBoards().size(), Matchers.equalTo(0));
        MatcherAssert.assertThat(secondaryAccount.getAccessibleBoards().size(), Matchers.equalTo(0));
    }

    @Test
    public void deleteBoard_boardDNE_incorrectBoardId() throws IOException {
        createAccountHelper("Joey", "p1zza", "joey@tribiani.com");
        Account targetAccount = accountRepo.findByEmail(email);
        Account secondaryAccount = accountRepo.findByEmail("joey@tribiani.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, targetAccount);
        boardService.addMember(true, "Joey", targetBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard, Matchers.notNullValue());
        MatcherAssert.assertThat(targetAccount.getAdminLevelBoards().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(secondaryAccount.getAccessibleBoards().size(), Matchers.equalTo(1));

        HttpServletResponse deleteBoardHttpResponse = mock(HttpServletResponse.class);
        boardService.deleteBoard(true, 100L, username, deleteBoardHttpResponse);
        MatcherAssert.assertThat(targetAccount.getAdminLevelBoards().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(secondaryAccount.getAccessibleBoards().size(), Matchers.equalTo(1));
    }

    @Test
    public void addMember_happyCase_singleAccount() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        Account targetAccount = accountRepo.findByUsername("Regina");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        boardService.addMember(true, "Regina", targetBoard.getId(), httpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(targetAccount.getAccessibleBoards().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(targetAccount.getAdminLevelBoards().size(), Matchers.equalTo(0));
    }

    @Test
    public void addMember_happyCase_twoAccounts() throws IOException {
        createAccountHelper("Regina", "Philange", "p@g.com");
        createAccountHelper("Richard", "MustacheK1ng", "rich@optometrist.com");
        Board targetBoard = boardRepo.findByNameAndOwner(boardName, accountRepo.findByUsername(username));
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(1));

        Account targetAccount1 = accountRepo.findByUsername("Regina");
        Account targetAccount2 = accountRepo.findByUsername("Richard");

        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        boardService.addMember(true, "Regina", targetBoard.getId(), httpResponse);
        boardService.addMember(true, "Richard", targetBoard.getId(), secondHttpResponse);
        MatcherAssert.assertThat(targetBoard.getMembers().size(), Matchers.equalTo(3));
        MatcherAssert.assertThat(targetAccount1.getAccessibleBoards().size(), Matchers.equalTo(1));
        MatcherAssert.assertThat(targetAccount2.getAccessibleBoards().size(), Matchers.equalTo(1));
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

    public void createBoardHelper(String username, String boardName) throws IOException {
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        Account targetAccount = accountRepo.findByUsername(username);
        boardService.createBoard(true, boardName, targetAccount.getUsername(), httpResponse);
    }
}

