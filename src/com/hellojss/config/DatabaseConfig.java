package com.hellojss.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseConfig {

    public static final String DEFAULT_JNDI_NAME = "java:comp/env/jdbc/HelloJSS";
    public static final String DEFAULT_URL        = "jdbc:postgresql://localhost:5432/hello_jss";
    public static final String DEFAULT_USER       = "postgres";
    public static final String DEFAULT_PASSWORD   = "P@ssw0rd";
    public static final String DEFAULT_AUTH_MODE  = "password";

    private static final String IAM_JDBC_URL_FORMAT =
            "jdbc:postgresql://%s:%s/%s?ssl=true&sslmode=require";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "PostgreSQL JDBC driver is not available in the runtime classpath.", e);
        }
    }

    public Connection openConnection() throws SQLException {
        if ("iam".equalsIgnoreCase(getAuthMode())) {
            return openIamConnection();
        }

        DataSource dataSource = lookupDataSource();
        if (dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException ignored) {
                // Fall back to direct JDBC for local/dev if container resource exists but is not configured.
            }
        }
        return DriverManager.getConnection(getJdbcUrl(), getJdbcUser(), getJdbcPassword());
    }

    private Connection openIamConnection() throws SQLException {
        String host   = getDbHost();
        String port   = getDbPort();
        String dbName = getDbName();
        String user   = getJdbcUser();
        String region = getAwsRegion();
        try {
            String token = new IamAuthTokenGenerator(region)
                    .generate(host, Integer.parseInt(port), user);
            String url = String.format(IAM_JDBC_URL_FORMAT, host, port, dbName);
            return DriverManager.getConnection(url, user, token);
        } catch (Exception e) {
            throw new SQLException(
                    "IAM-authenticated connection failed. "
                    + "Verify the EC2 IAM role has rds-db:connect permission: "
                    + e.getMessage(), e);
        }
    }

    public String getAuthMode() {
        return getSetting("hellojss.db.authMode", "HELLOJSS_DB_AUTH_MODE", DEFAULT_AUTH_MODE);
    }

    public String getDbHost() {
        return getSetting("hellojss.db.host", "HELLOJSS_DB_HOST", "localhost");
    }

    public String getDbPort() {
        return getSetting("hellojss.db.port", "HELLOJSS_DB_PORT", "5432");
    }

    public String getDbName() {
        return getSetting("hellojss.db.name", "HELLOJSS_DB_NAME", "hello_jss");
    }

    public String getAwsRegion() {
        return getSetting("hellojss.db.awsRegion", "HELLOJSS_AWS_REGION", "ap-southeast-1");
    }

    public String getJdbcUrl() {
        return getSetting("hellojss.db.url", "HELLOJSS_DB_URL", DEFAULT_URL);
    }

    public String getJdbcUser() {
        return getSetting("hellojss.db.user", "HELLOJSS_DB_USER", DEFAULT_USER);
    }

    public String getJdbcPassword() {
        return getSetting("hellojss.db.password", "HELLOJSS_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    public String getJndiName() {
        return getSetting("hellojss.db.jndiName", "HELLOJSS_DB_JNDI_NAME", DEFAULT_JNDI_NAME);
    }

    private DataSource lookupDataSource() {
        try {
            Object resource = new InitialContext().lookup(getJndiName());
            if (resource instanceof DataSource) return (DataSource) resource;
            return null;
        } catch (NamingException e) {
            return null;
        }
    }

    private String getSetting(String systemPropertyName, String environmentName,
                               String defaultValue) {
        String systemValue = System.getProperty(systemPropertyName);
        if (isPresent(systemValue)) return systemValue.trim();

        String environmentValue = System.getenv(environmentName);
        if (isPresent(environmentValue)) return environmentValue.trim();

        return defaultValue;
    }

    private boolean isPresent(String value) {
        return value != null && value.trim().length() > 0;
    }
}