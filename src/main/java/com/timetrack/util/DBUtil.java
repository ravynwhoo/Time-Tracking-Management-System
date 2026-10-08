package com.timetrack.util;

import com.timetrack.exception.DatabaseException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database utility class managing JDBC Connections.
 * Implements Singleton-style configuration loading and connection factory.
 */
public class DBUtil {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String dbDriver;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                dbUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/timetrack_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                dbUser = props.getProperty("db.username", "root");
                dbPassword = props.getProperty("db.password", "");
                dbDriver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Default fallback
                dbUrl = "jdbc:mysql://localhost:3306/timetrack_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                dbUser = "root";
                dbPassword = "";
                dbDriver = "com.mysql.cj.jdbc.Driver";
            }
            Class.forName(dbDriver);
        } catch (Exception e) {
            System.err.println("[DBUtil] Warning: Failed to load db.properties: " + e.getMessage());
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("[DBUtil] MySQL Driver not found: " + ex.getMessage());
            }
        }
    }

    /**
     * Obtains a standard JDBC Connection.
     * @return Connection object
     * @throws DatabaseException if connection cannot be established
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to database: " + e.getMessage(), e);
        }
    }

    /**
     * Safely closes a Connection.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBUtil] Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests whether the database connection is currently available.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
