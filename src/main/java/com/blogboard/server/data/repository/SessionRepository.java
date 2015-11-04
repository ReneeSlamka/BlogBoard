package com.blogboard.server.data.repository;


import com.blogboard.server.data.entity.Session;
import org.springframework.data.repository.CrudRepository;

public interface SessionRepository extends CrudRepository<Session, Long> {

    Session findBySessionId(String sessionId);

    Session findByAccountUsername(String accountUsername);
}