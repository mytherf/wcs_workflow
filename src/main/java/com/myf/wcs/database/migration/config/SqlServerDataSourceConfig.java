package com.myf.wcs.database.migration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import java.sql.*;

/**
 * @author H443745
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${database.type}'.equals('sqlserver')")
public class SqlServerDataSourceConfig extends AbstractDataSourceConfig {

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
        // DO Nothing
    }

    @Override
    protected void checkDatabase(Connection connection, String datasourceName, String username) throws SQLException {
        if (connection == null) {
            return;
        }

        String databaseSql = "SELECT name FROM sys.databases WHERE name = ?";

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

        String createDatabaseSql = "CREATE DATABASE " + datasourceName + " COLLATE Chinese_PRC_90_BIN2";
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

        String userSql = "SELECT * FROM sys.syslogins WHERE loginname = ?";

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

        String createUserLoginSql = "CREATE LOGIN  " + username + " WITH PASSWORD = '" + password + "'";
        Statement statementUserLogin = null;
        try {
            statementUserLogin = connection.createStatement();
            statementUserLogin.executeUpdate(createUserLoginSql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statementUserLogin != null) {
                statementUserLogin.close();
            }
        }

        String createUserSql = "CREATE USER " + username + " FOR LOGIN " + username;
        Statement statementUser = null;
        try {
            statementUser = connection.createStatement();
            statementUser.executeUpdate(createUserSql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statementUser != null) {
                statementUser.close();
            }
        }
    }

    @Override
    protected void grantPermission(Connection connection, String datasourceName, String username, String password) throws SQLException {
        if (connection == null) {
            return;
        }

        String databaseSql = "GRANT ALL PRIVILEGES ON " + datasourceName + " TO '" + username + "' AS sysadmin;";
        log.info(databaseSql);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(databaseSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
