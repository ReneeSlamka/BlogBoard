package com.blogboard.server.data.entity;

import com.blogboard.server.service.AppServiceHelper;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

@Entity
@Table(name="BOARD")
public class Board {
    @Id
    @Column(name="BOARD_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="NAME")
    private String name;

    @Column(name="DATE_CREATED")
    private String dateCreated;

    @Column(name="URL")
    private String url;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account owner;

    @ManyToMany(mappedBy = "accessibleBoards", cascade = CascadeType.PERSIST)
    private List<Account> members = new ArrayList<Account>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "POST_ID")
    private List<Post> posts;

    public Board() {
        super();
    }

    public Board(String name, Account owner, String dateCreated) {
        this.name = name;
        this.owner = owner;
        this.dateCreated = dateCreated;
        //this.addMember(owner);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }


    public Account getOwner() { return owner; }

    public void setOwner(Account newOwner) {
        this.owner = newOwner;
    }


    public String getDateCreated() { return dateCreated; }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }


    public void setUrl(String baseUrl) {
        this.url = baseUrl + File.separator +  id;
    }

    public String getUrl() { return url; }

    public  List<Account> getMembers() { return members; }

    public boolean addMember(Account newMember) {
        if (!members.contains(newMember)) {
            members.add(newMember);
            return true;
        }
        return false;
    }

    public boolean removeMember(Account memberToDelete) {
        if (members.contains(memberToDelete)) {
            members.remove(memberToDelete);
            return true;
        }
        return false;
    }

}
