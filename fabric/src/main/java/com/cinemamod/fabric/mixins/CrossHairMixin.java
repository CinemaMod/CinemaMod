package com.cinemamod.fabric.mixins;

import com.cinemamod.fabric.CinemaModClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CrossHairMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void renderCrosshair(DrawContext context, CallbackInfo ci) {
        if (CinemaModClient.getInstance().getScreenManager().hasActiveScreen()
                && CinemaModClient.getInstance().getVideoSettings().isHideCrosshair()) {
            ci.cancel();
        }
    }

}
