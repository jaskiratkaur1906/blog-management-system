package com.jaskirat.blog_management_system.controller;

import com.jaskirat.blog_management_system.dto.CommentRequest;
import com.jaskirat.blog_management_system.dto.CommentResponse;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.security.SecurityUtils;
import com.jaskirat.blog_management_system.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                      @Valid @RequestBody CommentRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        CommentResponse response = commentService.addComment(postId, request, currentUser);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId, @PathVariable Long commentId) {
        User currentUser = SecurityUtils.getCurrentUser();
        commentService.delete(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}