package com.jaskirat.blog_management_system.service;

import com.jaskirat.blog_management_system.dto.CommentResponse;
import com.jaskirat.blog_management_system.dto.PostRequest;
import com.jaskirat.blog_management_system.dto.PostResponse;
import com.jaskirat.blog_management_system.entity.Comment;
import com.jaskirat.blog_management_system.entity.Post;
import com.jaskirat.blog_management_system.entity.User;
import com.jaskirat.blog_management_system.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponse create(PostRequest request, User currentUser) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(currentUser); // author comes from the authenticated user, never from the request body

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    public List<PostResponse> getAll() {
        return postRepository.findAllWithComments() // the JOIN FETCH query from Step 3 - avoids N+1
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PostResponse getById(Long id) {
        Post post = postRepository.findByIdWithComments(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
        return toResponse(post);
    }

    public PostResponse update(Long id, PostRequest request, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new SecurityException("You can only edit your own posts");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return toResponse(postRepository.save(post));
    }

    public void delete(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));

        boolean isAuthor = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().getName().equals("ROLE_ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new SecurityException("You don't have permission to delete this post");
        }

        postRepository.delete(post);
    }

    private PostResponse toResponse(Post post) {
        List<CommentResponse> commentResponses = post.getComments().stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getText(),
                        c.getAuthor().getUsername(),
                        c.getCreatedAt()))
                .toList();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getUsername(),
                post.getCreatedAt(),
                commentResponses
        );
    }
}