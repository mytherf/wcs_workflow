package com.myf.wcs.database.migration.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import java.sql.*;

/**
 * @author H443745
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${database.type}'.equals('postgres')")
public class PostgresDataSourceConfig extends AbstractDataSourceConfig {

    @Value("${database.type}")
    private String datasourceType;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.admin.username}")
    private String adminName;

    @Value("${spring.datasource.admin.password}")
    private String adminPassword;

    @Override
    protected void checkSchema(Connection connection, String currentSchema) throws SQLException {
        if (connection == null) {
            return;
        }

        String schemaSql = "CREATE SCHEMA IF NOT EXISTS " + currentSchema + " AUTHORIZATION " + username;
        log.info(schemaSql);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(schemaSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void checkDatabase(Connection connection, String datasourceName, String username) throws SQLException {
        if (connection == null) {
            return;
        }

        String databaseSql = "SELECT datname FROM pg_database WHERE  datname = ?";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(databaseSql);
            statement.setString(1, datasourceName);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                //数据库不存在，创建数据库
                createDatabase(connection, datasourceName, username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }

        }
    }

    @Override
    protected void createDatabase(Connection connection, String datasourceName, String username) throws SQLException {
        if (connection == null) {
            return;
        }

        String createDatabaseSql = "CREATE DATABASE " + datasourceName + " OWNER " + username;
        log.info(createDatabaseSql);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createDatabaseSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void checkUser(Connection connection, String username, String password) throws SQLException {
        if (connection == null) {
            return;
        }

        String userSql = "SELECT * FROM pg_user WHERE  usename = ?";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(userSql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                //用户不存在，创建用户
                createUser(connection, username, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }

        }
    }

    @Override
    protected void createUser(Connection connection, String username, String password) throws SQLException {
        if (connection == null) {
            return;
        }

        String createUserSql = "create user " + username + " with password '" + password + "'";
        log.info(createUserSql);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createUserSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void grantPermission(Connection connection, String datasourceName, String username, String password) throws SQLException {
        // DO Nothing
    }

    @Override
    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getAdminName() {
        return adminName;
    }

    @Override
    public String getAdminPassword() {
        return adminPassword;
    }

    @Override
    public String getDatasourceType() {
        return datasourceType;
    }
}
