package com.blogboard.server.data.entity;

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
    private ArrayList<String> members;

    private ArrayList<String> posts;

    public Board() {
        super();
    }

    public Board(String name, String owner, String dateCreated, String baseUrl) {
        String urlEncodedName = name;
        try {
            name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
        }

        this.name = name;
        this.ownerUsername = owner;
        this.dateCreated = dateCreated;
        this.url = baseUrl + File.separator + "board=" +  urlEncodedName;
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

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    private void setUrl(String url) { this.url = url; }

    public String getUrl() { return url; }

    public String getDateCreated() { return dateCreated; }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void addMember(String newMember) {
        members.add(newMember);
    }

    public void removeMember(Account memberToDelete) {
        members.remove(memberToDelete);
    }

}
