package org.example.cucumber_demo.service;

import org.example.cucumber_demo.entity.Post;
import org.example.cucumber_demo.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository repository) {
        this.postRepository = repository;
    }

    @Override
    public List<Post> findAll() {
        return (List<Post>) postRepository.findAll();
    }

    @Override
    public Post findById(Integer id) throws Exception {
        return postRepository.findById(id)
                .orElseThrow(() -> new Exception("Post not found with id: " + id));
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Integer id, Post post) throws Exception {
        return postRepository.findById(id)
                .map(existingPost -> {
                    existingPost.setTitle(post.getTitle());
                    existingPost.setContent(post.getContent());
                    return postRepository.save(existingPost);
                })
                .orElseThrow(() -> new Exception("Post not found with id: " + id));
    }
}


