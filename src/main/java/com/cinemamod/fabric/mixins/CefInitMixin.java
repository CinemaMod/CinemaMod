package com.cinemamod.fabric.mixins;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.cef.CefUtil;
import com.cinemamod.fabric.cef.Platform;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

/**
 * A mixin is used here to load JCEF at the earliest point in the MC bootstrap process
 * See: net.minecraft.client.main.Main
 * This reduces issues with CEF initialization
 * Due to AWT issues on macOS, we cannot initialize CEF here
 */
@Mixin(Main.class)
public class CefInitMixin {

    private static void setupLibraryPath(Platform platform) throws IOException {
        // Check for development environment
        // i.e. cinemamod-repo/build/cef/<platform>
        File cefPlatformDir = new File("../build/cef/" + platform.getNormalizedName());
        if (cefPlatformDir.exists()) {
            System.setProperty("cinemamod.libraries.path", cefPlatformDir.getCanonicalPath());
            return;
        }

        // Check for .minecraft/mods/cinemamod-libraries directory
        File cinemaModLibrariesDir = new File("mods/cinemamod-libraries");
        if (!cinemaModLibrariesDir.exists()) {
            cinemaModLibrariesDir.mkdirs();
        }
        System.setProperty("cinemamod.libraries.path", cinemaModLibrariesDir.getCanonicalPath());
    }

    @Inject(at = @At("HEAD"), method = "main ([Ljava/lang/String;)V", remap = false)
    private static void cefInit(CallbackInfo info) {
        Platform platform = Platform.getPlatform();

        try {
            setupLibraryPath(platform);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Move to org.cef.CefApp
        if (platform.isLinux()) {
            System.loadLibrary("jawt");
        }

        if (platform.isLinux() || platform.isWindows()) {
            if (CefUtil.init()) {
                CinemaMod.LOGGER.info("Chromium Embedded Framework initialized");
            } else {
                CinemaMod.LOGGER.warn("Could not initialize Chromium Embedded Framework");
            }
        }
    }

}
