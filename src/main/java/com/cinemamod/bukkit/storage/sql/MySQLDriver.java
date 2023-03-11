package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.CinemaModConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDriver implements SQLDriver {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLDriver(CinemaModConfig config) {
        this.host = config.mysqlHost;
        this.port = config.mysqlPort;
        this.database = config.mysqlDatabase;
        this.username = config.mysqlUsername;
        this.password = config.mysqlPassword;
    }

    @Override
    public Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

}
