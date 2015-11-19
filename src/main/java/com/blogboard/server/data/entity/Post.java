package com.blogboard.server.data.entity;


import javax.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String author; //TODO: maybe change this to Account object
    private String datePosted;
    private String textContent;

    public Post() {
        super();
    }

    public Post(String name, String author, String datePosted) {
        this.name = name;
        this.author = author;
        this.datePosted = datePosted;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    private void setDateCreated(String dateCreated) {
        this.datePosted = dateCreated;
    }

    public String getDateCreated() {
        return datePosted;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextContent() {
        return textContent;
    }


}
