package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager
 * Supports both MySQL and SQLite databases
 */
public class DatabaseConnection {
    
    // Database configuration - Change these according to your setup
    private static final String DB_TYPE = "sqlite"; // "mysql" or "sqlite"
    
    // MySQL Configuration
    private static final String MYSQL_HOST = "localhost";
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_DATABASE = "expense_tracker";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = ""; // Change this
    
    // SQLite Configuration
    private static final String SQLITE_DB_PATH = "./database/expense_tracker.db";
    
    static {
        try {
            if (DB_TYPE.equalsIgnoreCase("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if (DB_TYPE.equalsIgnoreCase("sqlite")) {
                Class.forName("org.sqlite.JDBC");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Database driver not found!");
        }
        
        // Initialize database if using SQLite
        if (DB_TYPE.equalsIgnoreCase("sqlite")) {
            DatabaseInitializer.initialize();
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (DB_TYPE.equalsIgnoreCase("mysql")) {
            return getMySQLConnection();
        } else if (DB_TYPE.equalsIgnoreCase("sqlite")) {
            return getSQLiteConnection();
        }
        throw new SQLException("Invalid database type: " + DB_TYPE);
    }
    
    /**
     * Get MySQL connection
     */
    private static Connection getMySQLConnection() throws SQLException {
        String url = String.format(
            "jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
            MYSQL_HOST,
            MYSQL_PORT,
            MYSQL_DATABASE
        );
        System.out.println("Connecting to MySQL: " + url);
        return DriverManager.getConnection(url, MYSQL_USER, MYSQL_PASSWORD);
    }
    
    /**
     * Get SQLite connection
     */
    private static Connection getSQLiteConnection() throws SQLException {
        String url = "jdbc:sqlite:" + SQLITE_DB_PATH;
        System.out.println("Connecting to SQLite: " + url);
        return DriverManager.getConnection(url);
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get database type
     */
    public static String getDatabaseType() {
        return DB_TYPE;
    }
}
