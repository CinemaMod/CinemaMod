package com.cinemamod.api.net;

public interface ByteBufSerializable<T extends ByteBufSerializable> {

    T fromBytes(MinecraftByteBufWrapper buf);

    void toBytes(MinecraftByteBufWrapper buf);

}
