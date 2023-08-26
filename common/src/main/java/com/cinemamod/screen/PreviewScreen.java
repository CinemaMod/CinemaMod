package com.cinemamod.screen;

import com.cinemamod.fabric.block.PreviewScreenBlock;
import com.cinemamod.fabric.buffer.PacketByteBufSerializable;
import com.cinemamod.fabric.util.ImageUtil;
import com.cinemamod.fabric.video.VideoInfo;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class PreviewScreen implements PacketByteBufSerializable<PreviewScreen> {
    private int x;
    private int y;
    private int z;
    private String facing;
    private String staticTextureUrl;
    private String activeTextureUrl;

    @Nullable
    private VideoInfo videoInfo;

    private transient BlockPos blockPos; // used as a cache for performance
    private transient boolean unregistered;

    @Nullable
    private transient NativeImageBackedTexture staticTexture;
    @Nullable
    private transient NativeImageBackedTexture activeTexture;
    @Nullable
    private transient NativeImageBackedTexture thumbnailTexture;

    public PreviewScreen(int x, int y, int z, String facing, String staticTextureUrl, String activeTextureUrl) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.staticTextureUrl = staticTextureUrl;
        this.activeTextureUrl = activeTextureUrl;
    }

    public PreviewScreen() {

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

    public String getFacing() {
        return facing;
    }

    public String getStaticTextureUrl() {
        return staticTextureUrl;
    }

    public String getActiveTextureUrl() {
        return activeTextureUrl;
    }

    @Nullable
    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public boolean hasVideoInfo() {
        return videoInfo != null;
    }

    public void setVideoInfo(@Nullable VideoInfo videoInfo) {
        this.videoInfo = videoInfo;

        if (videoInfo == null || videoInfo.getThumbnailUrl() == null) {
            setThumbnailTexture(null);
        } else {
            ImageUtil.fetchImageTextureFromUrl(videoInfo.getThumbnailUrl()).thenAccept(this::setThumbnailTexture);
        }
    }

    public BlockPos getBlockPos() {
        if (blockPos == null) {
            blockPos = new BlockPos(x, y, z);
        }

        return blockPos;
    }

    @Nullable
    public NativeImageBackedTexture getStaticTexture() {
        if (staticTexture == null && staticTextureUrl != null) {
            ImageUtil.fetchImageTextureFromUrl(staticTextureUrl).thenAccept(texture -> staticTexture = texture);
        }

        return staticTexture;
    }

    @Nullable
    public NativeImageBackedTexture getActiveTexture() {
        if (activeTexture == null && activeTextureUrl != null) {
            ImageUtil.fetchImageTextureFromUrl(activeTextureUrl).thenAccept(texture -> activeTexture = texture);
        }

        return activeTexture;
    }

    @Nullable
    public NativeImageBackedTexture getThumbnailTexture() {
        return thumbnailTexture;
    }

    public void setThumbnailTexture(NativeImageBackedTexture thumbnailTexture) {
        if (this.thumbnailTexture != null) {
            this.thumbnailTexture.close();
        }
        this.thumbnailTexture = thumbnailTexture;
    }

    public void register() {
        if (MinecraftClient.getInstance().world == null) {
            return;
        }

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (MinecraftClient.getInstance().world.isChunkLoaded(chunkX, chunkZ)) {
            MinecraftClient.getInstance().world.setBlockState(getBlockPos(), PreviewScreenBlock.PREVIEW_SCREEN_BLOCK.getDefaultState());
        }

        ClientChunkEvents.CHUNK_LOAD.register((clientWorld, worldChunk) -> {
            if (unregistered) {
                return;
            }

            // If the loaded chunk has this screen block in it, place it in the world
            if (worldChunk.getPos().x == chunkX && worldChunk.getPos().z == chunkZ) {
                clientWorld.setBlockState(getBlockPos(), PreviewScreenBlock.PREVIEW_SCREEN_BLOCK.getDefaultState());
            }
        });
    }

    public void unregister() {
        unregistered = true;

        if (staticTexture != null) staticTexture.close();
        if (activeTexture != null) activeTexture.close();
        if (thumbnailTexture != null) thumbnailTexture.close();

        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.setBlockState(getBlockPos(), Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public PreviewScreen fromBytes(PacketByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        facing = buf.readString();
        staticTextureUrl = buf.readString();
        activeTextureUrl = buf.readString();
        return this;
    }

    @Override
    public void toBytes(PacketByteBuf buf) {
        throw new NotImplementedException("Not implemented on client");
    }
}
