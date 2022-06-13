package com.cinemamod.bukkit.storage.sql;

import com.cinemamod.bukkit.CinemaModConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLVideoStorage extends GenericSQLVideoStorage {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLVideoStorage(CinemaModConfig config) throws SQLException {
        this.host = config.mysqlHost;
        this.port = config.mysqlPort;
        this.database = config.mysqlDatabase;
        this.username = config.mysqlUsername;
        this.password = config.mysqlPassword;

        try (Connection connection = createConnection()) {
            createTables(connection);
        }
    }

    @Override
    protected Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

}
