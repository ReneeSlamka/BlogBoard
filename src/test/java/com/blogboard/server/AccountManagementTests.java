package com.blogboard.server;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.service.AppServiceHelper;
import com.blogboard.server.service.AuthenticationService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration

public class AccountManagementTests extends Mockito {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private AccountRepository accountRepo;


    String username = "Joey";
    String password = "W3L0v3P1zza!";
    String email = "joey@tribiani.com";
    HttpServletResponse httpResponse;


    @Before
    public void createTestAccount() throws IOException {
        HttpServletResponse httpCreateAccountResponse = mock(HttpServletResponse.class);
        accountService.createAccount(username, password, email, httpCreateAccountResponse);
        httpResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void changeEmail_happyCase() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo(email));
        accountService.changeEmail(true, username, "j@t.com", httpResponse);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo("j@t.com"));
    }

    @Test
    public void changeEmail_sameEmail() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo(email));
        BasicResponse response =  accountService.changeEmail(true, username, email, httpResponse);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo(email));
        MatcherAssert.assertThat(response.getMessage(), Matchers.nullValue());
    }

    @Test
    public void changeEmail_emailAlreadyInUse() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo(email));
        BasicResponse response =  accountService.changeEmail(true, username, email, httpResponse);
        MatcherAssert.assertThat(targetAccount.getEmail(), Matchers.equalTo(email));
        MatcherAssert.assertThat(response.getMessage(), Matchers.nullValue());
    }



    @Test
    public void changePassword_happyCase() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        MatcherAssert.assertThat(targetAccount.getPassword(), Matchers.equalTo(AppServiceHelper.hashString(password)));
        accountService.changePassword(true, username, password, "W3L0v3F00d", httpResponse);
        MatcherAssert.assertThat(targetAccount.getPassword(), Matchers.equalTo(AppServiceHelper.hashString("W3L0v3F00d")));
    }

    @Test
    public void changePassword_samePassword() throws IOException {
        Account targetAccount = accountRepo.findByUsername(username);
        MatcherAssert.assertThat(targetAccount.getPassword(), Matchers.equalTo(AppServiceHelper.hashString(password)));
        BasicResponse response = accountService.changePassword(true, username, password, password, httpResponse);
        MatcherAssert.assertThat(targetAccount.getPassword(),
                Matchers.equalTo(AppServiceHelper.hashString(password)));
        MatcherAssert.assertThat(response.getMessage(), Matchers.nullValue());
    }
}
