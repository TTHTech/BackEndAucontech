# Stage 1: Build với Maven
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /blog

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim AS runtime
WORKDIR /blog

# LẤY đúng file JAR do Maven tạo (trong target/)
COPY --from=build backend/target/blog-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
