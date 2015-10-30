package com.blogboard.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;
import org.springframework.stereotype.Service;

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

    public Account createAccount(Account newAccount) {

        if (newAccount.getId() != null) {
            // Can't create Greeting with specified ID value
            return null;
        }
        //Account savedAccount = accountRepo.save(newAccount);
        accountRepo.save(newAccount);
        return newAccount;
    }
}
