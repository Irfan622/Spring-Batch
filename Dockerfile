# Use the official Gradle image to build the application
# This image contains Gradle and OpenJDK
FROM gradle:8.8-jdk17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle files and download dependencies
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/
RUN gradle build --no-daemon

# Copy the source code into the container
COPY src /app/src

# Build the application
RUN gradle build --no-daemon

# Use the official OpenJDK image to run the application
# This image contains only the JDK runtime
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder image
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
