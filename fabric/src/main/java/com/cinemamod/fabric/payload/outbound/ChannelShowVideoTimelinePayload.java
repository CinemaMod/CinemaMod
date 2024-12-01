package com.cinemamod.fabric.payload.outbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ChannelShowVideoTimelinePayload implements CustomPayload {
    public static final IdCodec<ChannelShowVideoTimelinePayload> CHANNEL_SHOW_VIDEO_TIMELINE = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "show_video_timeline")),
            PacketCodec.ofStatic(
                    (b, p) -> {},
                    b -> new ChannelShowVideoTimelinePayload()
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_SHOW_VIDEO_TIMELINE.id();
    }
}