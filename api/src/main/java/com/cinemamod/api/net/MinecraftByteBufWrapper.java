package com.cinemamod.api.net;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public interface MinecraftByteBufWrapper {

    ByteBuf getUnderlyingBuffer();

    MinecraftByteBufWrapper writeVarInt(int i);

    int readVarInt();

    MinecraftByteBufWrapper writeUtf(String string);

    String readUtf();

    MinecraftByteBufWrapper writeUUID(UUID uuid);

    UUID readUUID();

}
