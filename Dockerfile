# --- Build Stage ---
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml and source code to build the executable JAR
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the Build stage
COPY --from=build /app/target/villageconnect-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (fallback default)
EXPOSE 8080

# Execute the Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
