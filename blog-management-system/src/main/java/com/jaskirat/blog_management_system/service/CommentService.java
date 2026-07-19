package com.jaskirat.blog_management_system.service;

import com.jaskirat.blog_management_system.dto.CommentRequest;
import com.jaskirat.blog_management_system.dto.CommentResponse;
import com.jaskirat.blog_management_system.entity.Comment;
import com.jaskirat.blog_management_system.entity.Post;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.repository.CommentRepository;
import com.jaskirat.blog_management_system.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponse addComment(Long postId, CommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthor(currentUser);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                saved.getText(),
                saved.getAuthor().getUsername(),
                saved.getCreatedAt()
        );
    }

    public void delete(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }
}