package com.cinemamod.fabric.payload.inbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.video.list.VideoListEntry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public record ChannelVideoListHistorySplit(List<VideoListEntry> entries) implements CustomPayload {
    public static final IdCodec<ChannelVideoListHistorySplit> CHANNEL_VIDEO_LIST_HISTORY_SPLIT = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "video_list_history_split")),
            PacketCodec.ofStatic(
                    (b, p) -> {
                        throw new NotImplementedException();
                    },
                    b -> {
                        List<VideoListEntry> entries = new ArrayList<>();
                        int length = b.readInt();
                        for (int i = 0; i < length; i++)
                            entries.add(new VideoListEntry().fromBytes(b));
                        b.clear();
                        return new ChannelVideoListHistorySplit(entries);
                    }
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_VIDEO_LIST_HISTORY_SPLIT.id();
    }
}