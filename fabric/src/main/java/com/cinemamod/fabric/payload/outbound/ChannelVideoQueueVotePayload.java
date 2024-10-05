package com.cinemamod.fabric.payload.outbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.video.VideoInfo;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChannelVideoQueueVotePayload(VideoInfo videoInfo, int voteType) implements CustomPayload {
    public static final IdCodec<ChannelVideoQueueVotePayload> CHANNEL_VIDEO_QUEUE_VOTE = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "video_queue_vote")),
            PacketCodec.ofStatic(
                    (b, p) -> {
                        p.videoInfo.toBytes(b);
                        b.writeInt(p.voteType);
                    },
                    b -> new ChannelVideoQueueVotePayload(new VideoInfo().fromBytes(b), b.readInt())
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_VIDEO_QUEUE_VOTE.id();
    }
}