package com.example.blog.post;

import com.example.blog.post.dto.PostRes;
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
    private final PostService service;
    private final UserRepository userRepo;

    public PostController(PostService s, UserRepository ur){
        this.service = s; this.userRepo = ur;
    }

    private UserEntity current(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username).orElseThrow();
    }

    // CREATE: vẫn nhận entity in (title/content), author = current user
    @PostMapping
    public PostRes create(@RequestBody PostEntity in){
        var saved = service.create(in.getTitle(), in.getContent(), current());
        return new PostRes(saved);
    }

    // LIST: trả Page<PostRes> (có author username)
    @GetMapping
    public Page<PostRes> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return service.listAll(pageable).map(PostRes::new);
    }

    // LIST MINE
    @GetMapping("/mine")
    public Page<PostRes> my(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return service.listMine(current().getId(), pageable).map(PostRes::new);
    }

    // DETAIL
    @GetMapping("/{id}")
    public PostRes detail(@PathVariable Long id){
        return new PostRes(service.get(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public PostRes update(@PathVariable Long id, @RequestBody PostEntity in){
        var saved = service.update(id, in.getTitle(), in.getContent(), current());
        return new PostRes(saved);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id, current());
        return ResponseEntity.noContent().build();
    }
}
