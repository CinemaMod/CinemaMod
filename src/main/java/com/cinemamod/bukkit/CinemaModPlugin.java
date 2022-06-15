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
import com.cinemamod.bukkit.storage.sql.MySQLVideoStorage;
import com.cinemamod.bukkit.storage.sql.SQLiteVideoStorage;
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
        cinemaModConfig.useMysql = getConfig().getBoolean("video-storage.mysql.use");
        cinemaModConfig.mysqlHost = getConfig().getString("video-storage.mysql.host");
        cinemaModConfig.mysqlPort = getConfig().getInt("video-storage.mysql.port");
        cinemaModConfig.mysqlDatabase = getConfig().getString("video-storage.mysql.database");
        cinemaModConfig.mysqlUsername = getConfig().getString("video-storage.mysql.username");
        cinemaModConfig.mysqlPassword = getConfig().getString("video-storage.mysql.password");

        if (cinemaModConfig.youtubeDataApiKey.length() != 39) {
            getLogger().warning("Invalid YouTube Data API V3 key. YouTube videos will not be able to be requested.");
        }

        FileVideoInfoFetcher.ffprobeCheck(this);

        theaterManager = new TheaterManager(this);
        theaterManager.loadFromConfig(getConfig().getConfigurationSection("theaters"));

        if (cinemaModConfig.useMysql) {
            try {
                videoStorage = new MySQLVideoStorage(cinemaModConfig);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (cinemaModConfig.useSqlite) {
            try {
                File dbFile = new File(getDataFolder(), "video_storage.db");
                videoStorage = new SQLiteVideoStorage(dbFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (videoStorage == null) {
            getLogger().warning("No video storage type found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerTheaterListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerVideoTimelineListener(this), this);
        getServer().getScheduler().runTaskTimer(this, () -> theaterManager.tickTheaters(), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new PlayerListUpdateTask(this), 20L, 20L);

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
