package com.example.blog.post;

import com.example.blog.user.UserEntity;
import com.example.blog.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
  private final PostService service; private final UserRepository userRepo;
  public PostController(PostService s, UserRepository ur){ this.service = s; this.userRepo = ur; }

  private UserEntity current(){
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userRepo.findByUsername(username).orElseThrow();
  }

  @PostMapping
  public PostEntity create(@RequestBody PostEntity in){
    return service.create(in.getTitle(), in.getContent(), current());
  }

  @GetMapping
  public Page<PostEntity> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
    return service.listAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
  }

  @GetMapping("/mine")
  public Page<PostEntity> my(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
    return service.listMine(current().getId(), PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public PostEntity detail(@PathVariable Long id){ return service.get(id); }

  @PutMapping("/{id}")
  public PostEntity update(@PathVariable Long id, @RequestBody PostEntity in){
    return service.update(id, in.getTitle(), in.getContent(), current());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id){
    service.delete(id, current());
    return ResponseEntity.noContent().build();
  }
}
