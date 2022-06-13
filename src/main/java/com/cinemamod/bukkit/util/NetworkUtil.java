package com.cinemamod.bukkit.util;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.buffer.PacketByteBufReimpl;
import com.cinemamod.bukkit.player.PlayerData;
import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.storage.VideoInfo;
import com.cinemamod.bukkit.storage.VideoRequest;
import com.cinemamod.bukkit.theater.StaticTheater;
import com.cinemamod.bukkit.theater.Theater;
import com.cinemamod.bukkit.theater.screen.PreviewScreen;
import com.cinemamod.bukkit.theater.screen.Screen;
import com.cinemamod.bukkit.video.Video;
import com.cinemamod.bukkit.video.queue.QueueVoteType;
import com.cinemamod.bukkit.video.queue.VideoQueue;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class NetworkUtil {

    /* OUTGOING */
    private static final String CHANNEL_SERVICES = "cinemamod:services";
    private static final String CHANNEL_SCREENS = "cinemamod:screens";
    private static final String CHANNEL_LOAD_SCREEN = "cinemamod:load_screen";
    private static final String CHANNEL_UNLOAD_SCREEN = "cinemamod:unload_screen";
    private static final String CHANNEL_UPDATE_PREVIEW_SCREEN = "cinemamod:update_preview_screen";
    private static final String CHANNEL_OPEN_SETTINGS_SCREEN = "cinemamod:open_settings_screen";
    private static final String CHANNEL_OPEN_HISTORY_SCREEN = "cinemamod:open_history_screen";
    private static final String CHANNEL_OPEN_PLAYLISTS_SCREEN = "cinemamod:open_playlists_screen";
    private static final String CHANNEL_VIDEO_LIST_HISTORY_SPLIT = "cinemamod:video_list_history_split";
    private static final String CHANNEL_VIDEO_LIST_PLAYLIST_SPLIT = "cinemamod:video_list_playlist_split";
    private static final String CHANNEL_VIDEO_QUEUE_STATE = "cinemamod:video_queue_state";
    /* INCOMING */
    private static final String CHANNEL_VIDEO_REQUEST = "cinemamod:video_request";
    private static final String CHANNEL_VIDEO_HISTORY_REMOVE = "cinemamod:video_history_remove";
    private static final String CHANNEL_VIDEO_PLAYLIST_CREATE = "cinemamod:video_playlist_create";
    private static final String CHANNEL_VIDEO_PLAYLIST_ADD = "cinemamod:video_playlist_add";
    private static final String CHANNEL_VIDEO_PLAYLIST_REMOVE = "cinemamod:video_playlist_remove";
    private static final String CHANNEL_VIDEO_QUEUE_VOTE = "cinemamod:video_queue_vote";
    private static final String CHANNEL_VIDEO_QUEUE_REMOVE = "cinemamod:video_queue_remove";
    private static final String CHANNEL_SHOW_VIDEO_TIMELINE = "cinemamod:show_video_timeline";

    public static void registerChannels(CinemaModPlugin cinemaModPlugin) {
        Messenger m = cinemaModPlugin.getServer().getMessenger();
        /* OUTGOING */
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_SERVICES);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_SCREENS);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_LOAD_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_UNLOAD_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_UPDATE_PREVIEW_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_OPEN_SETTINGS_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_OPEN_HISTORY_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_OPEN_PLAYLISTS_SCREEN);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_LIST_HISTORY_SPLIT);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_LIST_PLAYLIST_SPLIT);
        m.registerOutgoingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_QUEUE_STATE);
        /* INCOMING */
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_REQUEST, (s, player, bytes) -> {
            PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.wrappedBuffer(bytes));
            VideoInfo videoInfo = new VideoInfo().fromBytes(buf);
            cinemaModPlugin.getVideoStorage().searchVideoInfo(videoInfo.getServiceType(), videoInfo.getId()).thenAccept(search -> {
                if (!player.isOnline() || search == null) return;
                Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);
                if (theater == null || theater instanceof StaticTheater) return;
                theater.getVideoQueue().processPlayerRequest(search, player);
            });
        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_HISTORY_REMOVE, (s, player, bytes) -> {
            PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.wrappedBuffer(bytes));
            VideoInfo videoInfo = new VideoInfo().fromBytes(buf);
            PlayerData playerData = cinemaModPlugin.getPlayerDataManager().getData(player.getUniqueId());
            playerData.deleteHistory(videoInfo, cinemaModPlugin);
        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_PLAYLIST_CREATE, (s, player, bytes) -> {

        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_PLAYLIST_ADD, (s, player, bytes) -> {

        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_PLAYLIST_REMOVE, (s, player, bytes) -> {

        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_QUEUE_VOTE, (s, player, bytes) -> {
            PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.wrappedBuffer(bytes));
            VideoInfo videoInfo = new VideoInfo().fromBytes(buf);
            int voteType = buf.readInt();
            Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);
            if (theater != null) {
                Video video = theater.getVideoQueue().getVideo(videoInfo);
                if (video != null) {
                    QueueVoteType currentVote = video.getCurrentVote(player);

                    if (currentVote == null) {
                        if (voteType == -1) {
                            theater.getVideoQueue().downvoteVideo(player, video);
                        } else if (voteType == 1) {
                            theater.getVideoQueue().upvoteVideo(player, video);
                        }
                    } else if (currentVote.getValue() == voteType) {
                        theater.getVideoQueue().removeVote(player, video);
                    } else {
                        theater.getVideoQueue().removeVote(player, video);

                        if (voteType == -1) {
                            theater.getVideoQueue().downvoteVideo(player, video);
                        } else if (voteType == 1) {
                            theater.getVideoQueue().upvoteVideo(player, video);
                        }
                    }
                }
            }
        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_VIDEO_QUEUE_REMOVE, (s, player, bytes) -> {
            PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.wrappedBuffer(bytes));
            VideoInfo videoInfo = new VideoInfo().fromBytes(buf);
            Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);
            if (theater == null || theater instanceof StaticTheater) return;
            Video queuedVideo = theater.getVideoQueue().getVideo(videoInfo);
            if (queuedVideo == null || !queuedVideo.getRequester().equals(player) || !player.hasPermission("cinemamod.admin"))
                return;
            theater.getVideoQueue().unqueueVideo(queuedVideo);
        });
        m.registerIncomingPluginChannel(cinemaModPlugin, CHANNEL_SHOW_VIDEO_TIMELINE, (s, player, bytes) -> {
            Theater theater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);
            if (theater == null || theater instanceof StaticTheater) return;
            theater.showBossBars(cinemaModPlugin, player);
        });
    }

    public static void sendRegisterServicesPacket(JavaPlugin plugin, Player player) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        buf.writeInt(VideoServiceType.values().length);
        for (VideoServiceType type : VideoServiceType.values())
            type.toBytes(buf);
        player.sendPluginMessage(plugin, CHANNEL_SERVICES, buf.array());
    }

    public static void sendScreensPacket(JavaPlugin plugin, Player player, List<Screen> screens) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        buf.writeInt(screens.size());
        for (Screen screen : screens)
            screen.toBytes(buf);
        player.sendPluginMessage(plugin, CHANNEL_SCREENS, buf.array());
    }

    public static void sendLoadScreenPacket(JavaPlugin plugin, Player player, Screen screen, Video video) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        buf.writeInt(screen.getX());
        buf.writeInt(screen.getY());
        buf.writeInt(screen.getZ());
        video.toBytes(buf);
        player.sendPluginMessage(plugin, CHANNEL_LOAD_SCREEN, buf.array());
    }

    public static void sendUnloadScreenPacket(JavaPlugin plugin, Player player, Screen screen) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        buf.writeInt(screen.getX());
        buf.writeInt(screen.getY());
        buf.writeInt(screen.getZ());
        player.sendPluginMessage(plugin, CHANNEL_UNLOAD_SCREEN, buf.array());
    }

    public static void sendUpdatePreviewScreenPacket(JavaPlugin plugin, Player player, PreviewScreen previewScreen, @Nullable VideoInfo videoInfo) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        previewScreen.toBytes(buf);
        buf.writeBoolean(videoInfo != null);
        if (videoInfo != null) videoInfo.toBytes(buf);
        player.sendPluginMessage(plugin, CHANNEL_UPDATE_PREVIEW_SCREEN, buf.array());
    }

    public static void sendOpenSettingsScreenPacket(JavaPlugin plugin, Player player) {
        player.sendPluginMessage(plugin, CHANNEL_OPEN_SETTINGS_SCREEN, new byte[0]);
    }

    public static void sendOpenHistoryScreenPacket(JavaPlugin plugin, Player player) {
        player.sendPluginMessage(plugin, CHANNEL_OPEN_HISTORY_SCREEN, new byte[0]);
    }

    public static void sendOpenPlaylistsScreenPacket(JavaPlugin plugin, Player player) {
        player.sendPluginMessage(plugin, CHANNEL_OPEN_PLAYLISTS_SCREEN, new byte[0]);
    }

    public static void sendVideoListHistorySplitPacket(JavaPlugin plugin, Player player, List<VideoRequest> history) {
        for (List<VideoRequest> splitList : splitVideoRequests(history, 50)) {
            PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
            buf.writeInt(splitList.size());
            for (VideoRequest request : splitList)
                request.toBytes(buf);
            player.sendPluginMessage(plugin, CHANNEL_VIDEO_LIST_HISTORY_SPLIT, buf.array());
        }
    }

    public static void sendVideoListPlaylistSplitPacket(JavaPlugin plugin, Player player, List<VideoRequest> entries, String playlistName) {
        // TODO:
    }

    public static void sendVideoQueueStatePacket(JavaPlugin plugin, Player player, VideoQueue queue) {
        PacketByteBufReimpl buf = new PacketByteBufReimpl(Unpooled.buffer());
        queue.toBytes(buf, player);
        player.sendPluginMessage(plugin, CHANNEL_VIDEO_QUEUE_STATE, buf.array());
    }

    public static List<List<VideoRequest>> splitVideoRequests(List<VideoRequest> entries, int listSize) {
        List<List<VideoRequest>> splitEntries = new ArrayList<>();
        for (int i = 0; i < entries.size(); i = i + listSize) {
            if (i + listSize < entries.size())
                splitEntries.add(entries.subList(i, i + listSize));
            else
                splitEntries.add(entries.subList(i, entries.size()));
        }
        return splitEntries;
    }

}
