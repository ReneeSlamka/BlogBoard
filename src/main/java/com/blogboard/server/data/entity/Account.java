package com.blogboard.server.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ACCOUNT")
public class Account {

    @Id
    @Column(name="ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="EMAIL")
    private String email;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "ACCOUNT_BOARD", joinColumns = {@JoinColumn(name ="ACCOUNT_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BOARD_ID")})
    private List<Board> accessibleBoards = new ArrayList<Board>();


    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST)
    //@JoinColumn(name = "BOARD_ID")
    private List<Board> adminLevelBoards = new ArrayList<Board>();

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

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public boolean addAccessibleBoard(Board newBoard) {
        if(!accessibleBoards.contains(newBoard)) {
            this.accessibleBoards.add(newBoard);
            return true;
        }
        return  false;
    }

    public List<Board> getAccessibleBoards() {
        return accessibleBoards;
    }

    public boolean addAdminLevelBoard(Board newBoard) {
        if(!adminLevelBoards.contains(newBoard)) {
            this.adminLevelBoards.add(newBoard);
            return true;
        }
        return false;
    }

    public List<Board> getAdminLevelBoards() { return adminLevelBoards; }

    //TODO add function to update avatar image

}

