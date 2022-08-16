package com.bechris100.open_ransomware.network;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlServerConnect {

    private final String url, username, password;

    public SqlServerConnect(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
    }

    public List<SqlTable> receiveTable(String tableName) throws SQLException {
        return new ArrayList<>();
    }

}
