package com.blogboard.server.data.repository;

import com.blogboard.server.data.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.ArrayList;


public interface PostRepository extends CrudRepository<Post, Long> {

    ArrayList<Post> findByName(String name);

    ArrayList<Post> findByAuthor(String author);

    ArrayList<Post> findByDatePosted(String datePosted);
}
