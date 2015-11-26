package com.blogboard.server.data.repository;

import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.ArrayList;

public interface BoardRepository extends CrudRepository<Board, Long> {

    ArrayList<Board> findByName(String name);

    ArrayList<Board> findByOwner (Account owner);

    Board findByNameAndOwner(String name, Account owner);

    ArrayList<Board> findByMembersIn(ArrayList<Account> members);
}