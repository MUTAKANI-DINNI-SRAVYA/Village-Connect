# --- Build Stage ---
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Limit Maven memory to fit Render free tier (512MB RAM)
ENV MAVEN_OPTS="-Xmx384m -XX:+UseSerialGC"

# Copy pom.xml and source code to build the executable JAR
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# --- Run Stage ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the Build stage
COPY --from=build /app/target/villageconnect-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (fallback default)
EXPOSE 8080

# Execute the Spring Boot application with memory limits suitable for Render free tier
ENTRYPOINT ["java", "-Xmx384m", "-Xss512k", "-XX:+UseSerialGC", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
