package com.cinemamod.bukkit.storage.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDriver implements SQLDriver {

    private final File dbFile;

    public SQLiteDriver(File dbFile) throws IOException {
        this.dbFile = dbFile;

        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }
    }

    @Override
    public Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:sqlite:" + dbFile.getPath();
        return DriverManager.getConnection(jdbcUrl);
    }

}
