package com.blogboard.server.data.repository;

import com.blogboard.server.data.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findByUsername(String username);

    Account findByEmail(String email);
}
