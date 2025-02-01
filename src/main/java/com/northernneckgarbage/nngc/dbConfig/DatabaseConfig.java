package com.northernneckgarbage.nngc.dbConfig;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        logger.info("Configuring HikariCP connection pool...");
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName("NNGC-Pool");
        logger.info("HikariCP connection pool configured successfully");
        return dataSource;
    }
}