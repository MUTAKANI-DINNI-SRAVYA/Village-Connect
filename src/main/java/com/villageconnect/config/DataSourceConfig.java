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
        HikariConfig config = new HikariConfig();

        // 1. If Cloud DATABASE_URL (Render/Heroku/Railway) is present and spring.datasource.url is not overriding with an external DB
        if (databaseUrl != null && !databaseUrl.isBlank() && 
            (springDatasourceUrl == null || springDatasourceUrl.contains("h2:"))) {
            try {
                logger.info("Found DATABASE_URL environment variable. Configuring PostgreSQL DataSource.");
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
                String path = uri.getPath(); // e.g. /dbname
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;

                config.setJdbcUrl(jdbcUrl);
                if (username != null) config.setUsername(username);
                if (password != null) config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                return new HikariDataSource(config);
            } catch (Exception e) {
                logger.error("Failed to parse DATABASE_URL, falling back to standard datasource configuration: {}", e.getMessage());
            }
        }

        // 2. Standard Spring Datasource Configuration (MySQL, PostgreSQL, or local H2)
        String url = springDatasourceUrl;
        if (url == null || url.isBlank()) {
            url = "jdbc:h2:file:./data/villageconnect;DB_CLOSE_DELAY=-1;MODE=MySQL";
        }

        // If local H2 file database is used, ensure data directory exists
        if (url.startsWith("jdbc:h2:file:")) {
            try {
                new File("./data").mkdirs();
            } catch (Exception ignored) {
            }
        }

        config.setJdbcUrl(url);
        if (springDatasourceUsername != null && !springDatasourceUsername.isBlank()) {
            config.setUsername(springDatasourceUsername);
        }
        if (springDatasourcePassword != null) {
            config.setPassword(springDatasourcePassword);
        }

        return new HikariDataSource(config);
    }
}
