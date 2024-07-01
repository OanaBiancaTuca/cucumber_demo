package org.example.cucumber_demo.service;

import org.example.cucumber_demo.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> findAll();

    Post findById(Integer id) throws Exception;

    Post save(Post post);

    Post updatePost(Integer id,Post post) throws Exception;
}
