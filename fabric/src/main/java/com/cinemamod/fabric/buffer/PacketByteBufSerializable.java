package com.cinemamod.fabric.buffer;

import net.minecraft.network.PacketByteBuf;

public interface PacketByteBufSerializable<T extends PacketByteBufSerializable> {

    T fromBytes(PacketByteBuf buf);

    void toBytes(PacketByteBuf buf);

}
