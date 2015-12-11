package com.blogboard.server;

import com.blogboard.server.data.entity.Session;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.repository.SessionRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.service.AuthenticationService;
import com.blogboard.server.web.BasicResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Basic;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration

public class LoginTests extends Mockito {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private SessionRepository sessionRepo;

    String username = "Rachel";
    String password = "RaplphLaur3nF0rL1f3";
    String email = "rachel@green.com";
    HttpServletResponse httpResponse;

    @Before
    public void createTestAccount() throws IOException {

        HttpServletResponse createAccountHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount(username, email, password, createAccountHttpResponse);
        httpResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void login_happyCase_1a_singleAccount() throws IOException {
        authService.login(username,password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        MatcherAssert.assertThat("Login_Session: happy case", targetSession, Matchers.notNullValue());
    }

    @Test
    public void login_happyCase_1b_twoAccounts() throws IOException {
        HttpServletResponse secondCreateAccountHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount("Ross", "ross@gellar.com", "D1nosaur$Rul3", secondCreateAccountHttpResponse);

        authService.login(username,password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        HttpServletResponse secondLoginHttpResponse = mock(HttpServletResponse.class);
        authService.login("Ross","D1nosaur$Rul3",secondLoginHttpResponse);
        Session secondTargetSession = sessionRepo.findByAccountUsername("Ross");
        Long numSessions = sessionRepo.count();

        MatcherAssert.assertThat("Login_Session: happy case", targetSession, Matchers.notNullValue());
        MatcherAssert.assertThat("Login_Session: happy case", secondTargetSession, Matchers.notNullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(2L));
    }

    @Test
    public void login_2_secondLoginAttempt() throws IOException {
        authService.login(username,password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        authService.login(username, password, secondHttpResponse);
        Long numSessions = sessionRepo.count();

        MatcherAssert.assertThat(targetSession, Matchers.nullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(1L));
    }

    @Test
    public void login_3_incorrectUsername() throws IOException {
        authService.login("Bingaling",password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        Long numSessions = sessionRepo.count();
        MatcherAssert.assertThat(targetSession, Matchers.nullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(0L));
    }

    @Test
    public void login_4_incorrectPassword() throws IOException {
        authService.login(username,"Bingaling1984",httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        Long numSessions = sessionRepo.count();
        MatcherAssert.assertThat(targetSession, Matchers.nullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(0L));
    }

    @Test
    public void login_5_caseSensitivity() throws IOException {
        authService.login("chandler",password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        Long numSessions = sessionRepo.count();
        MatcherAssert.assertThat(targetSession, Matchers.nullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(0L));
    }

    @Test
    public void login_6_specialCharSensitivity() throws IOException {
        authService.login("Chándler",password,httpResponse);
        Session targetSession = sessionRepo.findByAccountUsername(username);
        Long numSessions = sessionRepo.count();
        MatcherAssert.assertThat(targetSession, Matchers.nullValue());
        MatcherAssert.assertThat(numSessions, Matchers.equalTo(0L));
    }
}
