package com.villageconnect.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.File;
import java.net.URI;

@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:#{null}}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:#{null}}")
    private String springDatasourceUsername;

    @Value("${spring.datasource.password:#{null}}")
    private String springDatasourcePassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        // 1. If Cloud DATABASE_URL is present (Render, Heroku, Railway)
        if (databaseUrl != null && !databaseUrl.isBlank() && 
            (springDatasourceUrl == null || springDatasourceUrl.contains("h2:"))) {
            try {
                logger.info("Found DATABASE_URL environment variable. Attempting PostgreSQL connection...");
                String cleanUrl = databaseUrl.trim();
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = "postgresql://" + cleanUrl.substring("postgres://".length());
                }
                URI uri = new URI(cleanUrl);
                String userInfo = uri.getUserInfo();
                String username = null;
                String password = null;
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }

                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = (uri.getPath() != null && !uri.getPath().isBlank()) ? uri.getPath() : "/villageconnect";
                String query = uri.getQuery();

                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                        .append(uri.getHost())
                        .append(":")
                        .append(port)
                        .append(path);

                if (query != null && !query.isBlank()) {
                    jdbcUrl.append("?").append(query);
                    if (!query.contains("sslmode=")) {
                        jdbcUrl.append("&sslmode=require");
                    }
                } else {
                    jdbcUrl.append("?sslmode=require");
                }

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl.toString());
                if (username != null) config.setUsername(username);
                if (password != null) config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setConnectionTimeout(15000);
                config.setMaximumPoolSize(5);

                HikariDataSource ds = new HikariDataSource(config);
                logger.info("Successfully connected to PostgreSQL database via DATABASE_URL!");
                return ds;
            } catch (Exception e) {
                logger.error("Failed to connect to PostgreSQL via DATABASE_URL: {}. Falling back to standard configuration.", e.getMessage());
            }
        }

        // 2. Standard Spring Datasource Configuration (MySQL or external PostgreSQL)
        if (springDatasourceUrl != null && !springDatasourceUrl.isBlank() && !springDatasourceUrl.startsWith("jdbc:h2:")) {
            try {
                logger.info("Attempting connection using spring.datasource.url: {}", springDatasourceUrl);
                HikariConfig config = new HikariConfig();
                String url = springDatasourceUrl;
                if (url.startsWith("jdbc:postgresql:") && !url.contains("sslmode=")) {
                    url += (url.contains("?") ? "&" : "?") + "sslmode=require";
                }
                config.setJdbcUrl(url);
                if (springDatasourceUsername != null && !springDatasourceUsername.isBlank()) {
                    config.setUsername(springDatasourceUsername);
                }
                if (springDatasourcePassword != null) {
                    config.setPassword(springDatasourcePassword);
                }
                if (url.contains("postgresql")) {
                    config.setDriverClassName("org.postgresql.Driver");
                } else if (url.contains("mysql")) {
                    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                }
                config.setConnectionTimeout(15000);
                config.setMaximumPoolSize(5);

                HikariDataSource ds = new HikariDataSource(config);
                logger.info("Successfully connected to external database via spring.datasource.url!");
                return ds;
            } catch (Exception e) {
                logger.error("Failed to connect to external datasource: {}. Falling back to embedded H2 database.", e.getMessage());
            }
        }

        // 3. Guaranteed Fallback to local embedded H2 file database
        return createH2FallbackDataSource();
    }

    private DataSource createH2FallbackDataSource() {
        logger.info("Initializing embedded H2 database (./data/villageconnect) as fallback...");
        try {
            new File("./data").mkdirs();
        } catch (Exception ignored) {
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:./data/villageconnect;DB_CLOSE_DELAY=-1;MODE=MySQL;AUTO_SERVER=TRUE");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(5);

        return new HikariDataSource(config);
    }
}
