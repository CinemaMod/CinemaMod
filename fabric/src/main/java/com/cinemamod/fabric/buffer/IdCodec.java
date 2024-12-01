package com.cinemamod.fabric.buffer;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record IdCodec<T extends CustomPayload>(CustomPayload.Id<T> id, PacketCodec<PacketByteBuf, T> codec) {}