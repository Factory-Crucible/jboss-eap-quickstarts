# Stage 1: Build the application
FROM gradle:8.6-jdk21 AS build
WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN gradle build --no-daemon -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-jammy

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms512m -Xmx1024m" \
    APP_HOME="/app"

WORKDIR ${APP_HOME}

# Create a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar ${APP_HOME}/app.jar

# Create directories for logs and data with proper permissions
RUN mkdir -p ${APP_HOME}/logs ${APP_HOME}/data && \
    chown -R spring:spring ${APP_HOME}

# Switch to non-root user
USER spring

# Expose the application port
EXPOSE 8080

# Set up a health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set the entrypoint script with environment-specific configurations
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar ${APP_HOME}/app.jar"]

# Labels for better maintainability
LABEL maintainer="developer@example.com" \
      version="1.0" \
      description="Spring Boot Kitchensink Application" \
      org.opencontainers.image.source="https://github.com/example/kitchensink-spring-boot"
