package com.cinemamod.fabric.payload.inbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.service.VideoService;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public record ChannelServicesPayload(List<VideoService> service) implements CustomPayload {
    public static final IdCodec<ChannelServicesPayload> CHANNEL_SERVICES = new IdCodec<>(
            new Id<>(Identifier.of(CinemaMod.MODID, "services")),
            PacketCodec.ofStatic(
                    (b, p) -> {
                        throw new NotImplementedException();
                    },
                    b -> {
                        List<VideoService> services = new ArrayList<>();
                        int length = b.readInt();
                        for (int i = 0; i < length; i++) {
                            services.add(new VideoService().fromBytes(b));
                        }
                        b.clear();
                        return new ChannelServicesPayload(services);
                    }
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_SERVICES.id();
    }
}