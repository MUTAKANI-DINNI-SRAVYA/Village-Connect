# --- Build Stage ---
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml and download dependencies to cache them in Docker layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the executable JAR
COPY src ./src
RUN mvn package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the Build stage
COPY --from=build /app/target/villageconnect-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (the default fallback port)
EXPOSE 8080

# Execute the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
