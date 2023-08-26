package com.cinemamod.mixins;

import com.cinemamod.fabric.CinemaModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class UnloadScreensMixin {
    @Shadow
    private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    private void disconnect(Screen screen, CallbackInfo info) {
        CinemaModClient.getInstance().getScreenManager().unloadAll();
        CinemaModClient.getInstance().getPreviewScreenManager().unloadAll();
        CinemaModClient.getInstance().getVideoServiceManager().unregisterAll();
        CinemaModClient.getInstance().getVideoListManager().reset();
        CinemaModClient.getInstance().getVideoQueue().clear();
    }
}
