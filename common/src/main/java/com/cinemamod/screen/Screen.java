package com.cinemamod.screen;

import com.cinemamod.block.ScreenBlock;
import com.cinemamod.buffer.PacketByteBufSerializable;
import com.cinemamod.video.Video;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Screen implements PacketByteBufSerializable<Screen> {
    private int x;
    private int y;
    private int z;
    private String facing;
    private float width;
    private float height;
    private boolean visible;
    private boolean muted;

    private transient List<PreviewScreen> previewScreens;
    private transient CefBrowserCinema browser;
    private transient Video video;
    private transient boolean unregistered;
    private transient BlockPos blockPos; // used as a cache for performance

    public Screen(int x, int y, int z, String facing, int width, int height, boolean visible, boolean muted) {
        this();
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.width = width;
        this.height = height;
        this.visible = visible;
        this.muted = muted;
    }

    public Screen() {
        previewScreens = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public BlockPos getPos() {
        if (blockPos == null) {
            blockPos = new BlockPos(x, y, z);
        }

        return blockPos;
    }

    public String getFacing() {
        return facing;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMuted() {
        return muted;
    }

    public List<PreviewScreen> getPreviewScreens() {
        return previewScreens;
    }

    public void addPreviewScreen(PreviewScreen previewScreen) {
        previewScreens.add(previewScreen);
    }

    public CefBrowserCinema getBrowser() {
        return browser;
    }

    public boolean hasBrowser() {
        return browser != null;
    }

    public void reload() {
        if (video != null) {
            loadVideo(video);
        }
    }

    public void loadVideo(Video video) {
        this.video = video;
        closeBrowser();
        browser = CefUtil.createBrowser(video.getVideoInfo().getVideoService().getUrl(), this);
    }

    public void closeBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
    }

    public Video getVideo() {
        return video;
    }

    public void setVideoVolume(float volume) {
        if (browser != null && video != null) {
            String js = video.getVideoInfo().getVideoService().getSetVolumeJs();

            // 0-100 volume
            if (js.contains("%d")) {
                js = String.format(js, (int) (volume * 100));
            }

            // 0.00-1.00 volume
            else if (js.contains("%f")) {
                js = String.format(js, volume);
            }

            browser.getMainFrame().executeJavaScript(js, browser.getURL(), 0);
        }
    }

    public void startVideo() {
        if (browser != null && video != null) {
            String startJs = video.getVideoInfo().getVideoService().getStartJs();

            if (startJs.contains("%s") && startJs.contains("%b")) {
                startJs = String.format(startJs, video.getVideoInfo().getId(), video.getVideoInfo().isLivestream());
            } else if (startJs.contains("%s")) {
                startJs = String.format(startJs, video.getVideoInfo().getId());
            }

            browser.getMainFrame().executeJavaScript(startJs, browser.getURL(), 0);

            // Seek to current time
            if (!video.getVideoInfo().isLivestream()) {
                long millisSinceStart = System.currentTimeMillis() - video.getStartedAt();
                long secondsSinceStart = millisSinceStart / 1000;
                if (secondsSinceStart < video.getVideoInfo().getDurationSeconds()) {
                    String seekJs = video.getVideoInfo().getVideoService().getSeekJs();

                    if (seekJs.contains("%d")) {
                        seekJs = String.format(seekJs, secondsSinceStart);
                    }

                    browser.getMainFrame().executeJavaScript(seekJs, browser.getURL(), 0);
                }
            }
        }
    }

    public void seekVideo(int seconds) {
        // TODO:
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void register() {
        if (Minecraft.getInstance().world == null) {
            return;
        }

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (MinecraftClient.getInstance().world.isChunkLoaded(chunkX, chunkZ)) {
            MinecraftClient.getInstance().world.setBlockState(getBlockPos(), ScreenBlock.SCREEN_BLOCK.getDefaultState());
        }

        ClientChunkEvents.CHUNK_LOAD.register((clientWorld, worldChunk) -> {
            if (unregistered) {
                return;
            }

            // If the loaded chunk has this screen block in it, place it in the world
            if (worldChunk.getPos().x == chunkX && worldChunk.getPos().z == chunkZ) {
                clientWorld.setBlockState(getBlockPos(), ScreenBlock.SCREEN_BLOCK.getDefaultState());
            }
        });
    }

    public void unregister() {
        unregistered = true;

        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.setBlockState(getBlockPos(), Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public Screen fromBytes(PacketByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        facing = buf.readString();
        width = buf.readFloat();
        height = buf.readFloat();
        visible = buf.readBoolean();
        muted = buf.readBoolean();
        return this;
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        throw new NotImplementedException("Not implemented on client");
    }
}
