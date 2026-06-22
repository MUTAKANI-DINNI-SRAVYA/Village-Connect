# VillageConnect Deployment & Setup Guide

This guide details the step-by-step instructions to set up, compile, run, and deploy the **VillageConnect** application.

---

## 1. Prerequisites
- **Java JDK 17** or higher installed. Verify with:
  ```bash
  java -version
  ```
- **MySQL Database Server** (v8.x) running locally or hosted externally.
- **Gemini API Key**: Obtain a key from [Google AI Studio](https://aistudio.google.com/).

---

## 2. Database Configuration

1. Log in to your MySQL terminal or database dashboard (e.g., phpMyAdmin, DBeaver) and create the database:
   ```sql
   CREATE DATABASE villageconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Open the file `src/main/resources/application.properties` and verify/update your MySQL connection parameters:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/villageconnect?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

---

## 3. Configure Gemini API Key

You can configure your Gemini API Key in two ways:

### Option A: Hardcode in Application Properties (For development)
Open `src/main/resources/application.properties` and paste your key directly:
```properties
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

### Option B: Set as Environment Variable (Recommended for production)
In `application.properties`, configure the key to read from environment:
```properties
gemini.api.key=${GEMINI_API_KEY}
```
Then, set the environment variable:
* **Windows (PowerShell):**
  ```powershell
  $env:GEMINI_API_KEY="your_api_key_here"
  ```
* **Linux / macOS:**
  ```bash
  export GEMINI_API_KEY="your_api_key_here"
  ```

---

## 4. Compile and Run Locally

### Using the Maven Wrapper (Recommended)
You do not need Maven globally installed. The project includes a maven wrapper.

1. **Clean and Compile:**
   ```bash
   ./mvnw clean compile
   ```
2. **Run Unit and Integration Tests:**
   The test suite executes against an in-memory H2 database so that it does not require MySQL to build.
   ```bash
   ./mvnw test
   ```
3. **Start the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start, connect to your MySQL database, compile tables automatically, and seed default test data (Admin, Shop Owners, Customers) if the database is empty.
4. **Access UI:**
   Open your browser and navigate to:
   `http://localhost:8080`

---

## 5. External Deployment (Build executable JAR)

To package the entire application (including backend and frontend static assets) into a single executable JAR file:

1. **Package JAR:**
   ```bash
   ./mvnw clean package -DskipTests
   ```
   This generates a file named `villageconnect-0.0.1-SNAPSHOT.jar` inside the `target/` directory.
2. **Run executable JAR:**
   Upload the JAR to your external hosting service (AWS EC2, Render, Azure VM, Heroku, etc.) and run:
   ```bash
   java -jar target/villageconnect-0.0.1-SNAPSHOT.jar
   ```

---

## 6. Seed Data Credentials
The application seeds default data on startup if the database is empty:
- **System Admin:** `admin@villageconnect.com` / `admin123`
- **Shop Owner 1:** `owner@villageconnect.com` / `owner123`
- **Shop Owner 2:** `owner2@villageconnect.com` / `owner123`
- **Customer:** `customer@villageconnect.com` / `customer123`
