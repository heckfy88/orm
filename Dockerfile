# Stage 1: Build the application
FROM maven:3.9.3-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the Spring Boot fat JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Spring Boot profile for Docker
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]