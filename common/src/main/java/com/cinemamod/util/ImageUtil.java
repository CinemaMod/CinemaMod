package com.cinemamod.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public final class ImageUtil {
    public static CompletableFuture<DynamicTexture> fetchImageTextureFromUrl(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                try (InputStream stream = new URL(url).openStream()) {
                    return new DynamicTexture(NativeImage.read(stream));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }
}
