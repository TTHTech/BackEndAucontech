package com.example.blog.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository repo;
  private final PasswordEncoder encoder = new BCryptPasswordEncoder();

  public UserService(UserRepository repo) {
    this.repo = repo;
  }

  public UserEntity register(String username, String rawPassword){
    if (repo.findByUsername(username).isPresent())
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username taken");
    UserEntity u = new UserEntity();
    u.setUsername(username);
    u.setPasswordHash(encoder.encode(rawPassword));
    return repo.save(u);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
    return User.withUsername(u.getUsername()).password(u.getPasswordHash()).authorities("USER").build();
  }
}
