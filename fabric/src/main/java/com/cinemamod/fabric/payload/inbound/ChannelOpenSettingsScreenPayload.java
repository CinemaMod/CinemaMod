package com.cinemamod.fabric.payload.inbound;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.buffer.IdCodec;
import com.cinemamod.fabric.gui.VideoSettingsScreen;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChannelOpenSettingsScreenPayload(VideoSettingsScreen screen) implements CustomPayload {
    public static final IdCodec<ChannelOpenSettingsScreenPayload> CHANNEL_OPEN_SETTINGS_SCREEN = new IdCodec<>(
            new CustomPayload.Id<>(Identifier.of(CinemaMod.MODID, "open_settings_screen")),
            PacketCodec.ofStatic(
                    (b, p) -> {},
                    b -> new ChannelOpenSettingsScreenPayload(new VideoSettingsScreen())
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL_OPEN_SETTINGS_SCREEN.id();
    }
}
