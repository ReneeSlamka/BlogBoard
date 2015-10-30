package com.blogboard.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

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
        Account savedAccount = accountRepo.save(newAccount);

        return savedAccount;
    }
}
