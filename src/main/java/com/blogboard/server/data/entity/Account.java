package com.blogboard.server.data.entity;

import javax.persistence.*;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null; //change later
    private String username;
    private String password;
    private String email;
    private boolean loggedIn = false;


    public Account() {
        super();
    }

    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    private void setPassword(String newPassword){
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.lang.String getEmail() {
        return email;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getLoggedIn(){
        return loggedIn;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    //TODO add function to update avatar image

}

