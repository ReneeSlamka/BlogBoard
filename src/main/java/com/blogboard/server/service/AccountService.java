package com.blogboard.server.service;

import com.blogboard.server.web.CreateAccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepo;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }

    public Account findOne(Long id) {
        Account account = accountRepo.findOne(id);
        return account;
    }

    public void delete(Long id) {
        accountRepo.delete(id);
    }

    public CreateAccountResponse createAccount(String username, String password, String email) {

        CreateAccountResponse createAccountResponse = new CreateAccountResponse();

        //check if account already exists
        if(accountRepo.findByUsername(username) != null) {
            createAccountResponse.setCreateAccountFailureMessage("username");
        } else if (accountRepo.findByEmail(email) != null) {
            createAccountResponse.setCreateAccountFailureMessage("email");
        } else {
            Account newAccount = new Account(username, password, email);
            Account savedAccount = accountRepo.save(newAccount);
            createAccountResponse.setCreateAccountSuccessMessage();
        }

        return createAccountResponse;
    }

}
