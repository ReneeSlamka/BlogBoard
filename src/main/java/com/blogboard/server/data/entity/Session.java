package com.blogboard.server.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;
    private String accountUsername;
    private String sessionId;

    private Session() {}

    public Session(String accountUsername, String sessionId) {

        /*If a user does not already have a session in place
        then their account username should not be listed in the
        corresponding session repo. After logging out their entry should
        be deleted. Must check this is true before making new session log.
        */
        this.accountUsername = accountUsername;
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountUsername() {
        return accountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String newSessionId) {
        this.sessionId = newSessionId;
    }

}
