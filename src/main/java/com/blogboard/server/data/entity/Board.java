package com.blogboard.server.data.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import javax.persistence.Column;

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

    @Column(name="members")
    private ArrayList<String> members;

    private ArrayList<String> posts;

    public Board() { super(); }

    public Board(String name, String owner) {
        this.name = name;
        this.ownerUsername = owner;
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
