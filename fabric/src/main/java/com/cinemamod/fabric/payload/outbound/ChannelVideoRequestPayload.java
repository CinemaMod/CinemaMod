package com.cinemamod.fabric.payload.outbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.video.VideoInfo;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChannelVideoRequestPayload(VideoInfo info) implements CustomPayload {
    public static final IdCodec<ChannelVideoRequestPayload> CHANNEL_VIDEO_REQUEST = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "video_request")),
            PacketCodec.ofStatic(
                    (b, p) -> p.info.toBytes(b),
                    b -> new ChannelVideoRequestPayload(new VideoInfo().fromBytes(b))
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_VIDEO_REQUEST.id();
    }
}