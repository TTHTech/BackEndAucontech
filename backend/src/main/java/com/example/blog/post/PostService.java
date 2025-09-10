package com.example.blog.post;

import com.example.blog.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostService {
  private final PostRepository repo;
  public PostService(PostRepository r){ this.repo = r; }

  public PostEntity create(String title, String content, UserEntity author){
    PostEntity p = new PostEntity();
    p.setTitle(title);
    p.setContent(content);
    p.setAuthor(author);
    return repo.save(p);
  }
  public Page<PostEntity> listAll(Pageable pageable){ return repo.findAll(pageable); }
  public Page<PostEntity> listMine(Long authorId, Pageable pageable){ return repo.findByAuthor_Id(authorId, pageable); }
  public PostEntity get(Long id){
    return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
  }
  public PostEntity update(Long id, String title, String content, UserEntity current){
    PostEntity p = get(id);
    if (!p.getAuthor().getId().equals(current.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
    p.setTitle(title); p.setContent(content);
    return repo.save(p);
  }
  public void delete(Long id, UserEntity current){
    PostEntity p = get(id);
    if (!p.getAuthor().getId().equals(current.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
    repo.delete(p);
  }
}
