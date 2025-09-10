package com.example.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
  Page<PostEntity> findByAuthor_Id(Long authorId, Pageable pageable);
}
