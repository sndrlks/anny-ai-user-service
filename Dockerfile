# === Stage 1: Build Stage ===
# Use the official Gradle image with JDK 17 as the builder
FROM gradle:7.6.0-jdk17 AS builder

# Set the working directory (adjust path as needed)
WORKDIR /home/gradle/project

# Copy only the necessary files to leverage Docker caching for dependencies
# Copy Gradle wrapper and build configuration files first
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts

# Download dependencies first (improves cache utilization)
RUN ./gradlew --no-daemon dependencies

# Now copy the full source code
COPY src src

# Build the application (skip tests if you want faster builds, e.g., with -x test)
RUN ./gradlew clean build --no-daemon -x test

# === Stage 2: Runtime Stage ===
# Use a minimal OpenJDK 17 image based on Alpine Linux for a smaller footprint
FROM openjdk:17-jdk-alpine

# Create a non-root user for enhanced security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR file from the builder stage.
# Adjust the path if your output JAR is named differently.
COPY --from=builder /home/gradle/project/build/libs/*.jar anny-ai-user-service.jar

# Expose the port your application listens on (adjust if needed)
EXPOSE 8080

# Change to non-root user
USER appuser

# Run the application
ENTRYPOINT ["java", "-jar", "anny-ai-user-service.jar"]
