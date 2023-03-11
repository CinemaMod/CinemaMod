package com.cinemamod.bukkit.util;

import com.cinemamod.bukkit.CinemaModPlugin;
import com.cinemamod.bukkit.theater.Theater;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ProtocolLibUtil {

    private static ProtocolManager protocolManager;

    private static ProtocolManager getProtocolManager() {
        if (protocolManager == null) {
            protocolManager = ProtocolLibrary.getProtocolManager();
        }

        return protocolManager;
    }

    public static void registerSoundPacketListener(CinemaModPlugin cinemaModPlugin) {
        getProtocolManager().addPacketListener(
                new PacketAdapter(cinemaModPlugin, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                            Player player = event.getPlayer();
                            int soundX = event.getPacket().getIntegers().read(0);
                            int soundY = event.getPacket().getIntegers().read(1);
                            int soundZ = event.getPacket().getIntegers().read(2);
                            Location soundLocation = new Location(player.getWorld(), soundX, soundY, soundZ);

                            Theater soundTheater = null;
                            Theater playerTheater = cinemaModPlugin.getTheaterManager().getCurrentTheater(player);

                            for (Theater theater : cinemaModPlugin.getTheaterManager().getTheaters()) {
                                if (theater.regionsContain(soundLocation)) {
                                    soundTheater = theater;
                                    break;
                                }
                            }

                            boolean shouldPlay = false;

                            // If sound played in the same theater as the player
                            if (soundTheater != null && soundTheater.equals(playerTheater)) {
                                shouldPlay = true;
                            }
                            // If sound and player are not in a theater
                            else if (soundTheater == null && playerTheater == null) {
                                shouldPlay = true;
                            }

                            if (!shouldPlay) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
    }

}
