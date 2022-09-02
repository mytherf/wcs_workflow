package com.myf.wcs.database.migration.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.myf.wcs.database.migration.utils.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author H443745
 */
@Slf4j
public abstract class AbstractDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        log.info("init datasource...{}", getDatasourceType());
        String userName = getUsername();
        String password = getPassword();
        String datasourceUrl = getDatasourceUrl();
        String driverClassName = getDriverClassName();

        DruidDataSource datasource = DruidDataSourceBuilder.create().build();
        datasource.setUrl(datasourceUrl);
        datasource.setUsername(userName);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        Connection connection = null;
        try {
            Class.forName(driverClassName);

            HashMap<String, String> datasourceMap = JdbcUtils.getDbInfo(datasourceUrl);
            String host = datasourceMap.get(JdbcUtils.HOST);
            String port = datasourceMap.get(JdbcUtils.PORT);

            // 通过默认数据库连接(管理员)
            log.info("getConnection..., host={},port={}", host, port);
            connection = DriverManager.getConnection(host + ":" + port + "/", getAdminName(), getAdminPassword());

            // 创建用户
            log.info("checkUser..., userName={}", userName);
            checkUser(connection, userName, password);

            // 创建数据库
            String datasourceName = datasourceMap.get(JdbcUtils.DB_NAME);
            log.info("checkDatabase..., datasourceName={}", datasourceName);
            checkDatabase(connection, datasourceName, userName);

            // 创建schema
            String currentSchema = datasourceMap.get(JdbcUtils.CURRENT_SCHEMA);
            if (StrUtil.isNotBlank(currentSchema)) {
                log.info("checkSchema..., currentSchema={}", currentSchema);
                checkSchema(connection, currentSchema);
            }

            //赋权
            grantPermission(connection, datasourceName, userName, password);

        } catch (Exception e) {
            log.error("");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return datasource;
    }

    /**
     * grant permission
     *
     * @param connection     Connection
     * @param datasourceName datasource name
     * @param username       username
     * @param password       password
     * @throws SQLException
     */
    protected abstract void grantPermission(Connection connection, String datasourceName, String username, String password) throws SQLException;

    /**
     * get datasource url
     *
     * @return
     */
    protected abstract String getDatasourceUrl();

    /**
     * get driver class name
     *
     * @return
     */
    protected abstract String getDriverClassName();

    /**
     * get user name
     *
     * @return
     */
    protected abstract String getUsername();

    /**
     * get password
     *
     * @return
     */
    protected abstract String getPassword();

    /**
     * get admin name
     *
     * @return
     */
    protected abstract String getAdminName();

    /**
     * get admin password
     *
     * @return
     */
    protected abstract String getAdminPassword();

    /**
     * get datasource type
     *
     * @return
     */
    protected abstract String getDatasourceType();

    /**
     * check schema
     *
     * @param connection    Connection
     * @param currentSchema current schema
     * @throws SQLException
     */
    protected abstract void checkSchema(Connection connection, String currentSchema) throws SQLException;

    /**
     * check database
     *
     * @param connection     Connection
     * @param datasourceName datasource name
     * @param username       user name
     * @throws SQLException
     */
    protected abstract void checkDatabase(Connection connection, String datasourceName, String username) throws SQLException;

    /**
     * create database
     *
     * @param connection     Connection
     * @param datasourceName datasource name
     * @param username       user name
     * @throws SQLException
     */
    protected abstract void createDatabase(Connection connection, String datasourceName, String username) throws SQLException;

    /**
     * check user
     *
     * @param connection Connection
     * @param username   user name
     * @param password   password
     * @throws SQLException
     */
    protected abstract void checkUser(Connection connection, String username, String password) throws SQLException;

    /**
     * create user
     *
     * @param connection Connection
     * @param username   user name
     * @param password   password
     * @throws SQLException
     */
    protected abstract void createUser(Connection connection, String username, String password) throws SQLException;

}
