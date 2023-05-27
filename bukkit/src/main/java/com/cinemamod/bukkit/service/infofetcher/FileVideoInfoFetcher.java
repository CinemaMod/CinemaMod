package com.cinemamod.bukkit.service.infofetcher;

import com.cinemamod.bukkit.service.VideoServiceType;
import com.cinemamod.bukkit.video.VideoInfo;
import org.bytedeco.javacpp.Loader;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class FileVideoInfoFetcher extends VideoInfoFetcher {

    // ffprobe.exe -i https://example.com/path/to/video.mp4 -show_entries format=duration -v quiet -of csv="p=0"

    private static final String ffprobePath;

    static {
        ffprobePath = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
    }

    private final String url;
    private final String requesterUsername;

    public FileVideoInfoFetcher(String permission, String url, String requesterUsername) {
        super(permission);
        this.url = url;
        this.requesterUsername = requesterUsername;
    }

    @Override
    public CompletableFuture<VideoInfo> fetch() {
        return CompletableFuture.supplyAsync(() -> {
            try {
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
}
