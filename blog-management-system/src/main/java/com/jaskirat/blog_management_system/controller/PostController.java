package com.jaskirat.blog_management_system.controller;

import com.jaskirat.blog_management_system.dto.PostRequest;
import com.jaskirat.blog_management_system.dto.PostResponse;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.security.SecurityUtils;
import com.jaskirat.blog_management_system.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        PostResponse response = postService.create(request, currentUser);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(postService.update(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        postService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}