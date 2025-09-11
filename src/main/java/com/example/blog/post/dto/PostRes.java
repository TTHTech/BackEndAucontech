package com.example.blog.post.dto;

import com.example.blog.post.PostEntity;

public record PostRes(Long id, String title, String content, String author) {
    public PostRes(PostEntity p) {
        this(
                p.getId(),
                p.getTitle(),
                p.getContent(),
                p.getAuthor() != null ? p.getAuthor().getUsername() : null
        );
    }
}
