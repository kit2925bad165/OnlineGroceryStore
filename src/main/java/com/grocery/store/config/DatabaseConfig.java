package com.grocery.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.driver-class-name}")
    private String dbDriver;

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j dependency.", e);
        }
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}
