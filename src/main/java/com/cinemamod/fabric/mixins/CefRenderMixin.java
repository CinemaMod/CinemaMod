package com.cinemamod.fabric.mixins;

import com.cinemamod.fabric.cef.CefUtil;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CefRenderMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (CefUtil.isInit()) {
            CefUtil.getCefApp().N_DoMessageLoopWork();
        }
    }

}