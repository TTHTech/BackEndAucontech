package com.example.blog.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
  @Value("${jwt.secret}") private String secret;
  @Value("${jwt.expiration-ms}") private long expirationMs;

  public String generateToken(String username){
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String validateAndGetUsername(String token){
    return Jwts.parserBuilder().setSigningKey(getKey()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  private Key getKey(){ return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
}
