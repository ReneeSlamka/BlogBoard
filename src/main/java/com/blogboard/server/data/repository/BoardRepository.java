package com.blogboard.server.data.repository;

import com.blogboard.server.data.entity.Board;
import com.blogboard.server.data.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface BoardRepository extends CrudRepository<Board, Long> {

    Board findByName(String name);

    ArrayList<Board> findByOwnerUsername (String ownerUsername);

    Board findByNameAndOwnerUsername(String name, String ownerUsername);

}
