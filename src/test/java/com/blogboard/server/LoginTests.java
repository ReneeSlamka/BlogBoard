package com.blogboard.server;

import com.blogboard.server.data.repository.AccountRepository;
import com.blogboard.server.service.AccountService;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.web.BasicResponse;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

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
    private AccountRepository accountRepo;

    String username = "Chandler";
    String password = "JanniceIsEvil1993";
    String email = "chandler@bing.com";
    BasicResponse response;
    HttpServletResponse httpResponse;

    @Before
    public void createTestAccount() throws IOException {
        httpResponse = mock(HttpServletResponse.class);
        response = accountService.createAccount("Rachel", "RaplphLaur3nF0rL1f3", "rachel@green.com", httpResponse);
    }
}
