package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.video.VideoInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FileVideoInfoFetcher extends VideoInfoFetcher {

    // ffprobe.exe -i https://example.com/path/to/video.mp4 -show_entries format=duration -v quiet -of csv="p=0"
    private static final String ffprobeWinMirror = "https://cinemamod-static.ewr1.vultrobjects.com/ffmpeg/ffprobe.exe";
    private static boolean ffprobeAvailable;

    static {
        ffprobeAvailable = false;
    }

    private CinemaModPlugin cinemaModPlugin;
    private String url;
    private String requesterUsername;

    public FileVideoInfoFetcher(CinemaModPlugin cinemaModPlugin, String permission, String url, String requesterUsername) {
        super(permission);
        this.cinemaModPlugin = cinemaModPlugin;
        this.url = url;
        this.requesterUsername = requesterUsername;
    }

    @Override
    public CompletableFuture<VideoInfo> fetch() {
        if (!ffprobeAvailable) {
            cinemaModPlugin.getLogger().warning("A video file was unable to be requested because the server is lacking ffprobe." +
                    "If on Windows, ffprobe must be installed in the CinemaMod plugin directory as ffprobe.exe." +
                    "If on Linux, ffprobe must be available at /usr/bin/ffprobe.");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                final String ffprobePath;
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    ffprobePath = new File(cinemaModPlugin.getDataFolder(), "ffprobe.exe").getAbsolutePath();
                } else {
                    ffprobePath = "/usr/bin/ffprobe";
                }

                Process process = new ProcessBuilder(
                        ffprobePath,
                        "-i",
                        url,
                        "-show_entries",
                        "format=duration",
                        "-v",
                        "quiet",
                        "-of",
                        "default=noprint_wrappers=1:nokey=1"
                ).redirectErrorStream(true).start();

                String result = readInput(process.getInputStream()).trim();

                if (!result.isEmpty()) {
                    try {
                        VideoServiceType serviceType = VideoServiceType.FILE;
                        String id = url;
                        String title = url;
                        String thumbnailUrl = "https://cinemamod-static.ewr1.vultrobjects.com/images/file_thumbnail.jpg";
                        float durationSeconds = Float.parseFloat(result);
                        return new VideoInfo(serviceType, id, title, requesterUsername, thumbnailUrl, (long) durationSeconds);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    private static String readInput(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream)) {
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine());
            return stringBuilder.toString();
        }
    }

    public static void ffprobeCheck(JavaPlugin plugin) {
        CompletableFuture.runAsync(() -> {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                File file = new File(plugin.getDataFolder(), "ffprobe.exe");

                if (!file.exists()) {
                    plugin.getLogger().info("Downloading ffprobe for Windows...");

                    try (InputStream in = new URL(ffprobeWinMirror).openStream()) {
                        Files.copy(in, Paths.get(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    Set<PosixFilePermission> perms = new HashSet<>();
                    perms.add(PosixFilePermission.OWNER_READ);
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    perms.add(PosixFilePermission.OWNER_EXECUTE);

                    try {
                        Files.setPosixFilePermissions(file.toPath(), perms);
                    } catch (IOException e) {
                        // Ignore
                    }

                    plugin.getLogger().info("ffprobe for Windows downloaded!");
                } else {
                    plugin.getLogger().info("Found ffprobe for Windows!");
                }
            } else {
                File ffprobeBin = new File("/usr/bin/ffprobe");

                if (!ffprobeBin.exists()) {
                    return;
                }
            }

            ffprobeAvailable = true;
        });
    }

}
