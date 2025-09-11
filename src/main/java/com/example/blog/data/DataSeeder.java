package com.example.blog.data;

import com.example.blog.post.PostEntity;
import com.example.blog.post.PostRepository;
import com.example.blog.user.Role;
import com.example.blog.user.UserEntity;
import com.example.blog.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, PostRepository posts) {
        return args -> {
            PasswordEncoder encoder = new BCryptPasswordEncoder();

            UserEntity admin = users.findByUsername("admin").orElseGet(() -> {
                UserEntity u = new UserEntity();
                u.setUsername("admin");
                u.setPasswordHash(encoder.encode("admin123"));
                u.setRole(Role.ROLE_ADMIN);
                return users.save(u);
            });

            UserEntity user = users.findByUsername("user").orElseGet(() -> {
                UserEntity u = new UserEntity();
                u.setUsername("user");
                u.setPasswordHash(encoder.encode("user123"));
                u.setRole(Role.ROLE_USER);
                return users.save(u);
            });

            for (int i = 1; i <= 10; i++) {
                String uname = "user" + i;
                if (users.findByUsername(uname).isEmpty()) {
                    UserEntity u = new UserEntity();
                    u.setUsername(uname);
                    u.setPasswordHash(encoder.encode("pass" + i));
                    u.setRole(Role.ROLE_USER);
                    users.save(u);
                }
            }

            if (posts.count() == 0) {
                // Lấy lại 1 vài tác giả cho đa dạng
                UserEntity author1 = users.findByUsername("user1").orElse(user);
                UserEntity author2 = users.findByUsername("user2").orElse(user);
                UserEntity author3 = users.findByUsername("admin").orElse(admin);

                for (int i = 1; i <= 10; i++) {
                    PostEntity p = new PostEntity();
                    p.setTitle("Sample Post #" + i);
                    p.setContent("This is sample content for post #" + i + ". Seeded by DataSeeder.");
                    if (i % 3 == 1) p.setAuthor(author3);
                    else if (i % 3 == 2) p.setAuthor(author1);
                    else p.setAuthor(author2);

                    posts.save(p);
                }
            }
        };
    }
}
