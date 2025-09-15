# Step 1: Build the JAR file
# Use Gradle image with OpenJDK 21 to build the project
FROM gradle:8.10-jdk21-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and configuration files first (for better caching)
COPY gradle/ ./gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle ./

# Copy source code and resources
COPY src ./src

# Make gradlew executable (in case it's not)
RUN chmod +x gradlew

# Run Gradle build to generate the JAR file
RUN ./gradlew clean bootJar -x test --no-daemon

# Step 2: Run the application
# Use a smaller JDK 21 image to run the built JAR
FROM openjdk:21-jdk-slim

# Set the working directory for the app
WORKDIR /app

# Copy the generated JAR file from the build stage to the runtime stage
COPY --from=builder /app/build/libs/msa-client-wflux-0.0.1-SNAPSHOT.jar /app/myapp.jar

# Expose the application port
EXPOSE 8081


# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]