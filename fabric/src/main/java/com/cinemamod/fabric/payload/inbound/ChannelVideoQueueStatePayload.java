package com.cinemamod.fabric.payload.inbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.video.VideoInfo;
import com.cinemamod.fabric.video.queue.QueuedVideo;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public record ChannelVideoQueueStatePayload(List<QueuedVideo> queue) implements CustomPayload {
    public static final IdCodec<ChannelVideoQueueStatePayload> CHANNEL_VIDEO_QUEUE_STATE = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "video_queue_state")),
            PacketCodec.ofStatic(
                    (b, p) -> {
                        throw new NotImplementedException();
                    },
                    b -> {
                        List<QueuedVideo> videos = new ArrayList<>();
                        int length = b.readInt();
                        for (int i = 0; i < length; i++) {
                            VideoInfo videoInfo = new VideoInfo().fromBytes(b);
                            int score = b.readInt();
                            int clientState = b.readInt();
                            boolean owner = b.readBoolean();
                            videos.add(new QueuedVideo(videoInfo, score, clientState, owner));
                        }
                        b.clear();
                        return new ChannelVideoQueueStatePayload(videos);
                    }
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_VIDEO_QUEUE_STATE.id();
    }
}