package com.cinemamod.bukkit;

import com.cinemamod.bukkit.command.HistoryCommand;
import com.cinemamod.bukkit.command.VolumeCommand;
import com.cinemamod.bukkit.command.theater.*;
import com.cinemamod.bukkit.listener.PlayerJoinQuitListener;
import com.cinemamod.bukkit.listener.PlayerTheaterListener;
import com.cinemamod.bukkit.listener.PlayerVideoTimelineListener;
import com.cinemamod.bukkit.player.PlayerDataManager;
import com.cinemamod.bukkit.service.infofetcher.FileVideoInfoFetcher;
import com.cinemamod.bukkit.storage.VideoStorage;
import com.cinemamod.bukkit.storage.sql.MySQLDriver;
import com.cinemamod.bukkit.storage.sql.SQLDriver;
import com.cinemamod.bukkit.storage.sql.SQLiteDriver;
import com.cinemamod.bukkit.storage.sql.video.SQLVideoStorage;
import com.cinemamod.bukkit.task.PlayerListUpdateTask;
import com.cinemamod.bukkit.theater.TheaterManager;
import com.cinemamod.bukkit.util.NetworkUtil;
import com.cinemamod.bukkit.util.ProtocolLibUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CinemaModPlugin extends JavaPlugin {

    private CinemaModConfig cinemaModConfig;
    private TheaterManager theaterManager;
    private VideoStorage videoStorage;
    private PlayerDataManager playerDataManager;

    public CinemaModConfig getCinemaModConfig() {
        return cinemaModConfig;
    }

    public TheaterManager getTheaterManager() {
        return theaterManager;
    }

    public VideoStorage getVideoStorage() {
        return videoStorage;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        cinemaModConfig = new CinemaModConfig();
        cinemaModConfig.youtubeDataApiKey = getConfig().getString("youtube-data-api-key");
        cinemaModConfig.enableTabTheaterList = getConfig().getBoolean("enable-tab-theater-list");
        cinemaModConfig.useMysql = getConfig().getBoolean("storage.mysql.use");
        cinemaModConfig.mysqlHost = getConfig().getString("storage.mysql.host");
        cinemaModConfig.mysqlPort = getConfig().getInt("storage.mysql.port");
        cinemaModConfig.mysqlDatabase = getConfig().getString("storage.mysql.database");
        cinemaModConfig.mysqlUsername = getConfig().getString("storage.mysql.username");
        cinemaModConfig.mysqlPassword = getConfig().getString("storage.mysql.password");

        cinemaModConfig.autogenCubicRegions = getConfig().getBoolean("autogenCubicRegions");

        if (cinemaModConfig.youtubeDataApiKey.length() != 39) {
            getLogger().warning("Invalid YouTube Data API V3 key. YouTube videos will not be able to be requested.");
        }

        FileVideoInfoFetcher.ffprobeCheck(this);

        theaterManager = new TheaterManager(this);
        theaterManager.loadFromConfig(getConfig().getConfigurationSection("theaters"));

        SQLDriver sqlDriver = null;

        if (cinemaModConfig.useMysql) {
            sqlDriver = new MySQLDriver(cinemaModConfig);
        } else if (cinemaModConfig.useSqlite) {
            File dbFile = new File(getDataFolder(), "video_storage.db");
            try {
                sqlDriver = new SQLiteDriver(dbFile);
            } catch (IOException ignored) {
                getLogger().warning("Unable to create or load database file");
            }
        }

        if (sqlDriver == null) {
            getLogger().warning("Could not initialize video storage");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            videoStorage = new SQLVideoStorage(sqlDriver);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerTheaterListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerVideoTimelineListener(this), this);
        getServer().getScheduler().runTaskTimer(this, () -> theaterManager.tickTheaters(), 20L, 20L);

        if (cinemaModConfig.enableTabTheaterList) {
            getServer().getScheduler().runTaskTimer(this, new PlayerListUpdateTask(this), 20L, 20L);
        }

        getCommand("request").setExecutor(new RequestCommand(this));
        getCommand("forceskip").setExecutor(new ForceSkipCommand(this));
        getCommand("voteskip").setExecutor(new VoteSkipCommand(this));
        getCommand("lockqueue").setExecutor(new LockQueueCommand(this));
        getCommand("volume").setExecutor(new VolumeCommand(this));
        getCommand("protect").setExecutor(new ProtectCommand(this));
        getCommand("playing").setExecutor(new PlayingCommand(this));
        getCommand("history").setExecutor(new HistoryCommand(this));

        NetworkUtil.registerChannels(this);
        ProtocolLibUtil.registerSoundPacketListener(this);
    }

}
