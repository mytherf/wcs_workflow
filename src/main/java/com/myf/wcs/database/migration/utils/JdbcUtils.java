package com.myf.wcs.database.migration.utils;

import java.util.HashMap;


/**
 * @author H443745
 */
public class JdbcUtils {

    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DB_NAME = "dbName";
    public static final String CURRENT_SCHEMA = "currentSchema";

    private JdbcUtils() {
    }

    public static HashMap<String, String> getDbInfo(String url) {
        HashMap<String, String> result = new HashMap<>(10);

        String[] split = url.split(":");
        String host = String.format("%s:%s:%s", split[0], split[1], split[2]);
        result.put(HOST, host);

        String[] portSplit = split[3].split("/");
        String port = portSplit[0];
        result.put(PORT, port);

        String[] databaseSplit = portSplit[1].split("\\?");
        String dbName = databaseSplit[0];
        result.put(DB_NAME, dbName);

        if (databaseSplit.length > 1) {
            String params = databaseSplit[1];
            String[] paramsSplit = params.split("&");
            for (String param : paramsSplit) {
                String[] paramSplit = param.split("=");
                result.put(paramSplit[0], paramSplit[1]);
            }
        }

        return result;
    }
}
