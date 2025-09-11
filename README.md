# Blog - Backend

## 📦 Tổng Quan Dự Án
**Tên dự án**: Blog Backend  
**Mô tả**:  
- Backend **Spring Boot 3** theo kiến trúc **RESTful API**.  
- Chức năng: **Auth** (đăng ký/đăng nhập JWT), **Posts** (CRUD + phân quyền tác giả), **Admin** (quản lý user, phân trang + tìm kiếm).  
- Bảo mật bằng **Spring Security + JWT**.  
- ORM **JPA/Hibernate**, DB **MySQL** (prod) / **H2** (dev).  

---

## 🧰 Công Nghệ Sử Dụng

- **Java 17**
- **Spring Boot 3.3.x**
- **Spring Web**, **Spring Data JPA**, **Spring Security**
- **jjwt 0.11.5** (JWT)
- **H2**, **MySQL Connector/J**
- **Validation**, **Lombok**

## 📂 Cấu Trúc Thư Mục Backend

```plaintext
src/
├─ main/
│  ├─ java/com/example/blog/
│  │  ├─ BlogApplication.java          # Điểm khởi chạy Spring Boot
│  │  │
│  │  ├─ config/                       # Cấu hình bảo mật & JWT
│  │  │  ├─ CorsConfig.java
│  │  │  ├─ SecurityConfig.java
│  │  │  └─ JwtUtil.java
│  │  │
│  │  ├─ data/                         # Seeder khởi tạo dữ liệu
│  │  │  └─ DataSeeder.java
│  │  │
│  │  ├─ user/                         # Quản lý người dùng
│  │  │  ├─ AuthController.java        # Đăng ký, đăng nhập, /me
│  │  │  ├─ UserEntity.java
│  │  │  ├─ UserRepository.java
│  │  │  ├─ UserService.java
│  │  │  ├─ Role.java
│  │  │  └─ dto/                       # DTO cho auth
│  │  │     ├─ AuthRequest.java
│  │  │     ├─ AuthResponse.java
│  │  │     └─ RegisterRequest.java
│  │  │
│  │  ├─ post/                         # Quản lý bài viết
│  │  │  ├─ PostController.java
│  │  │  ├─ PostService.java
│  │  │  ├─ PostRepository.java
│  │  │  ├─ PostEntity.java
│  │  │  └─ dto/
│  │  │     └─ PostRes.java
│  │  │
│  │  ├─ admin/                        # Quản lý user cho admin
│  │  │  ├─ AdminUserController.java
│  │  │  └─ dto/
│  │  │     └─ UserAdminDtos.java
│  │  │
│  │  └─ ... (các package khác nếu có)
│  │
│  └─ resources/
│     ├─ application.yml               # Cấu hình DB, JPA, JWT
│     └─ static / templates (nếu cần)
│
└─ test/java/com/example/blog/         # Unit test (chưa triển khai nhiều)
```

### Maven (pom.xml - trích phần quan trọng)
```xml
<dependencies>
  <!-- Web -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>

  <!-- JPA -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>

  <!-- Security -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>

  <!-- Validation -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>

  <!-- JWT -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>

  <!-- DB -->
  <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
  </dependency>
</dependencies>
```

---

## ⚙️ Cấu Hình Ứng Dụng

> File: `src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blogdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate.dialect: org.hibernate.dialect.MySQL8Dialect

jwt:
  secret: change-this-to-a-very-long-random-secret-change-me-1234567890abcd
  expiration-ms: 86400000
```

### CORS
```java
@Bean
public CorsFilter corsFilter() {
  var source = new UrlBasedCorsConfigurationSource();
  var config = new CorsConfiguration();
  config.setAllowCredentials(true);
  config.setAllowedOriginPatterns(List.of("http://localhost:3000"));
  config.setAllowedHeaders(List.of("*"));
  config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
  source.registerCorsConfiguration("/**", config);
  return new CorsFilter(source);
}
```

### Security Rules
```java
http
  .authorizeHttpRequests(authz -> authz
      .requestMatchers("/api/auth/**").permitAll()
      .requestMatchers("/api/admin/**").hasRole("ADMIN")
      .anyRequest().authenticated()
  )
  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

---

## ▶️ Cách Chạy

### Dev (H2)
```bash
mvn spring-boot:run
```
- Console: [http://localhost:8080/h2](http://localhost:8080/h2)

### Prod (MySQL)
```sql
CREATE DATABASE blogdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
```bash
mvn clean package
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

---

## 🔑 Xác Thực (JWT)

- Sau khi `POST /api/auth/login` thành công, client cần gắn:
```
Authorization: Bearer <token>
```
- Token hết hạn theo `jwt.expiration-ms`.

---

## 📚 API Endpoints

### Auth (public)
#### `POST /api/auth/register`
```json
{
  "username": "user1",
  "password": "123456"
}
```

#### `POST /api/auth/login`
```json
{
  "username": "user1",
  "password": "123456"
}
```
Trả về:
```json
{
  "token": "xxx.yyy.zzz",
  "username": "user1"
}
```

#### `GET /api/auth/me`
- Header: `Authorization: Bearer <token>`
- Trả về:
```json
{
  "id": 1,
  "username": "user1",
  "role": "ROLE_USER"
}
```

---

### Posts (yêu cầu JWT)

#### `GET /api/posts?page=0&size=10`
```json
{
  "content": [
    { "id": 1, "title": "Post 1", "content": "Nội dung", "author": "user1" }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

#### `POST /api/posts`
```json
{
  "title": "Sample",
  "content": "This is sample content"
}
```

#### `PUT /api/posts/{id}`
```json
{
  "title": "Updated",
  "content": "Updated content"
}
```

#### `DELETE /api/posts/{id}` → 204 No Content

#### `GET /api/posts/mine` → chỉ trả bài viết của user hiện tại.

---

### Admin (ROLE_ADMIN)

#### `GET /api/admin/users?page=0&size=10&q=abc`
- Trả về danh sách user phân trang.

#### `GET /api/admin/users/{id}`
```json
{ "id": 1, "username": "user1", "role": "ROLE_USER" }
```

#### `POST /api/admin/users`
```json
{
  "username": "newuser",
  "password": "123456",
  "role": "ROLE_ADMIN"
}
```

#### `PUT /api/admin/users/{id}`
```json
{
  "password": "newpass",
  "role": "ROLE_USER"
}
```

#### `DELETE /api/admin/users/{id}` → 204 No Content

---

## 🧪 Test với Postman

1. Gửi `POST /api/auth/register` tạo user mới.  
2. Gửi `POST /api/auth/login` → copy `token`.  
3. Tạo **collection** trong Postman, thêm variable `token`.  
4. Mỗi request protected thêm Header:
```
Authorization: Bearer {{token}}
```
5. Test các API Posts / Admin theo role.

