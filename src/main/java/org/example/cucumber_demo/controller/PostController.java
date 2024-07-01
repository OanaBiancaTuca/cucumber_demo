package org.example.cucumber_demo.controller;

import org.example.cucumber_demo.entity.Post;
import org.example.cucumber_demo.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPost() {
        List<Post> posts = postService.findAll();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable(value = "id") Integer id) throws Exception {
        Post post = postService.findById(id);
        return post != null ? new ResponseEntity<>(post, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Post> savePost(@RequestBody Post post) {
        Post savedPost = postService.save(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable(value = "id") Integer id,@RequestBody Post post) throws Exception {
        Post updatedPost = postService.updatePost(id,post);
        return new ResponseEntity<>(updatedPost, HttpStatus.CREATED);
    }


}