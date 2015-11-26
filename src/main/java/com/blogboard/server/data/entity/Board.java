package com.blogboard.server.data.entity;

import com.blogboard.server.service.AppServiceHelper;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    private String name;
    private String dateCreated;
    private String url;

    @OneToOne(targetEntity=Account.class, mappedBy="board", fetch=FetchType.EAGER)
    private Account owner;

    @OneToMany(targetEntity=Account.class, mappedBy="board", fetch=FetchType.LAZY)
    private List<Account> members = new ArrayList<Account>();

    //keep like this for now, in future might have different types of posts and want loose coupling
    //private ArrayList<String> posts;

    public Board() {
        super();
    }

    public Board(String name, Account owner, String dateCreated) {
        this.name = name;
        this.owner = owner;
        this.dateCreated = dateCreated;
        members.add(owner);
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
