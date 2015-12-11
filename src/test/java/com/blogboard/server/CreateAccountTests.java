package com.blogboard.server;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.web.BasicResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.entity.Account;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.junit.Before;
import org.springframework.boot.test.IntegrationTest;
import com.jayway.restassured.RestAssured;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ContextConfiguration;
import java.util.Collection;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {WebConfig.class, PersistenceContext.class})
@WebAppConfiguration
public class CreateAccountTests extends Mockito {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepo;

    String username = "Chandler";
    String password = "JaniceIsEvil1993";
    String email = "chandler@bing.com";
    HttpServletResponse httpResponse;

    @Before
    public void setupResponse() {
        httpResponse = mock(HttpServletResponse.class);
    }

    @Test
    public void createAccount_happyCase_1a_singleAccount() throws IOException {
        accountService.createAccount(username, password, email, httpResponse);
        Account targetAccount = accountRepo.findByUsername(username);

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount.getEmail(), Matchers.equalTo(email));
    }

    @Test
    public void createAccount_happyCase_1b_multipleAccounts() throws IOException {
        accountService.createAccount(username, password, email, httpResponse);
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount("Monica", "NeatFreak1994", "monica@gellar.com", secondHttpResponse);

        Account targetAccount1 = accountRepo.findByUsername(username);
        Account targetAccount2 = accountRepo.findByUsername("Monica");
        Long numSavedAccounts = accountRepo.count();

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount1, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount2, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", numSavedAccounts, Matchers.equalTo(2L));
    }


    @Test
    public void createAccount_2_specialChars() throws IOException {
        accountService.createAccount("Chándler", password, email, httpResponse);
        Account targetAccount = accountRepo.findByUsername("Chándler");

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount.getUsername(), Matchers.equalTo("Chándler"));
    }


    @Test
    public void createAccount_3_usernameAlreadyInUse() throws IOException {
        accountService.createAccount(username, password, email, httpResponse);
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount(username, password, "g@g.com", secondHttpResponse);

        Account targetAccount = accountRepo.findByUsername(username);
        Account secondAcount = accountRepo.findByEmail("g@g.com");
        Long numSavedAccounts = accountRepo.count();

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", secondAcount, Matchers.nullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", numSavedAccounts, Matchers.equalTo(1L));
    }


    @Test
    public void createAccount_4_emailAlreadyInUse() throws IOException {
        accountService.createAccount(username, password, email, httpResponse);
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount("Joey", password, email, secondHttpResponse);

        Account targetAccount = accountRepo.findByUsername(username);
        Long numSavedAccounts = accountRepo.count();

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", numSavedAccounts, Matchers.equalTo(1L));
    }

    @Test
    public void createAccount_5_happyCase_passwordAlreadyInUse() throws IOException {
        accountService.createAccount(username, password, email, httpResponse);
        HttpServletResponse secondHttpResponse = mock(HttpServletResponse.class);
        accountService.createAccount("Joey", password, "joey@tribiani.com", secondHttpResponse);

        Account targetAccount1 = accountRepo.findByUsername(username);
        Account targetAccount2 = accountRepo.findByUsername("Joey");
        Long numSavedAccounts = accountRepo.count();

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount1, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount2, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Repository: happy case", numSavedAccounts, Matchers.equalTo(2L));
    }
}