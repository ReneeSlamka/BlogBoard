package com.blogboard.server.data.entity;


import javax.persistence.*;

@Entity
@Table(name="POST")
public class Post {

    @Id
    @Column(name="POST_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="TITLE")
    private String title;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account author; //TODO: maybe change this to Account object

    @Column(name = "TIME_STAMP")
    private String timeStamp;

    //NOTE: use of columnDefinition = "blob" or "TEXT" is NOT portable, replace with length = #number later
    @Lob
    @Column(name = "TEXT_CONTENT", length = 4096)
    private String textContent;

    public Post() {
        super();
    }

    public Post(String title, Account author, String timeStamp) {
        this.title = title;
        this.author = author;
        this.timeStamp = timeStamp;
    }

    private void setTitle(String name) {
        this.title = name;
    }

    public String getTitle() {
        return title;
    }

    private void setAuthor(Account author) {
        this.author = author;
    }

    public Account getAuthor() {
        return author;
    }

    private void setTimeStamp(String dateCreated) {
        this.timeStamp = dateCreated;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextContent() {
        return textContent;
    }


}
