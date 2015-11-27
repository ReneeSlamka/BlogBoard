package com.blogboard.server.data.repository;

import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.ArrayList;


public interface PostRepository extends CrudRepository<Post, Long> {

    ArrayList<Post> findByTitle(String title);

    ArrayList<Post> findByAuthor(Account author);

    ArrayList<Post> findByTimeStamp(String timeStamp);
}
