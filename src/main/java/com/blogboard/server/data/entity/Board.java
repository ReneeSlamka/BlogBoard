package com.blogboard.server.data.entity;

import com.blogboard.server.service.AppServiceHelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import javax.persistence.Column;
import java.io.File;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="owner")
    private String ownerUsername;

    private String dateCreated;

    private String url;

    @Column(name="members")
    private ArrayList<String> members = new ArrayList<String>();;

    //keep like this for now, in future might have different types of posts and want loose coupling
    private ArrayList<String> posts;

    public Board() {
        super();
    }

    public Board(String name, String owner, String dateCreated) {
        this.name = name;
        this.ownerUsername = owner;
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


    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setownerUsername(String newOwner) {
        this.ownerUsername = newOwner;
    }


    public String getDateCreated() { return dateCreated; }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }


    public void setUrl(String baseUrl) {
        this.url = baseUrl + File.separator +  id;
    }

    public String getUrl() { return url; }


    public ArrayList<String> getMembers() {
        return members;
    }


    public boolean addMember(String newMember) {
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
