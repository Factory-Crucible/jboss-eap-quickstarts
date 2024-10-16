# Build stage
FROM openjdk:21-jdk-slim AS build
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM openjdk:21-jre-slim
WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port the app runs on
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1