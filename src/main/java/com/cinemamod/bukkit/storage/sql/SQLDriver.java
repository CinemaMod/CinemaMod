package com.cinemamod.bukkit.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLDriver {

    Connection createConnection() throws SQLException;

}
