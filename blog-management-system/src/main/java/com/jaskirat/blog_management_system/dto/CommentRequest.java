package com.jaskirat.blog_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment text is required")
    private String text;
}