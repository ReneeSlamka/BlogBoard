package com.blogboard.server;
import com.blogboard.server.configuration.WebConfig;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.configuration.PersistenceContext;
import com.blogboard.server.web.BasicResponse;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.Matchers.*;
import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.data.entity.Account;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class, WebConfig.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })

public class AccountManagementTests extends Mockito {

    private AccountRepository accountRepo;
    private AccountService accountService;

    @Autowired(required = true)
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    @Autowired(required = true)
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }


    /*
    *======Create Account Tests======
    */

    String username = "Chandler";
    String password = "JanniceIsEvil1993";
    String email = "chandler@bing.com";

    @Test
    public void createAccount_happyCase() throws IOException {

        BasicResponse response = new BasicResponse();
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        response = accountService.createAccount(accountRepo, username, password, email, httpResponse);
        Account targetAccount = accountRepo.findByUsername(username);

        MatcherAssert.assertThat("Create Account_Repository: happy case", targetAccount, Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_Response: happy case", response.getMessage(), Matchers.notNullValue());
        MatcherAssert.assertThat("Create Account_HttpResponse: happy case", httpResponse.getStatus(),
                Matchers.equalTo(HttpServletResponse.SC_CREATED));
    }

    /*
    *======Login Tests======
    */

}