package com.cinemamod.fabric.mixins;

import com.cinemamod.downloader.CinemaModDownloader;
import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.cef.CefUtil;
import net.minecraft.client.main.Main;
import org.cef.OS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A mixin is used here to load JCEF at the earliest point in the MC bootstrap process
 * See: net.minecraft.client.main.Main
 * This reduces issues with CEF initialization
 * Due to AWT issues on MacOS, we cannot initialize CEF here
 */
@Mixin(Main.class)
public class CefInitMixin {

    private static void setupLibraryPath() {
        Path minecraftPath = Paths.get("");
        Path modsPath = minecraftPath.resolve("mods");
        Path cinemaModLibrariesPath = modsPath.resolve("cinemamod-libraries");

        if (Files.notExists(cinemaModLibrariesPath)) {
            try {
                Files.createDirectory(cinemaModLibrariesPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.setProperty("cinemamod.libraries.path", cinemaModLibrariesPath.toAbsolutePath().toString());
    }

    @Inject(at = @At("HEAD"), method = "main ([Ljava/lang/String;)V", remap = false)
    private static void cefInit(CallbackInfo info) {
        setupLibraryPath();

        CinemaModDownloader.main(new String[]{});

        // TEMP HACK
        if (OS.isLinux()) {
            System.load("/usr/lib/jvm/java-17-openjdk-17.0.3.0.7-1.fc36.x86_64/lib/libjawt.so");
        }

        if (OS.isWindows() || OS.isLinux()) {
            if (CefUtil.init()) {
                CinemaMod.LOGGER.info("Chromium Embedded Framework initialized");
            } else {
                CinemaMod.LOGGER.warn("Could not initialize Chromium Embedded Framework");
            }
        }
    }

}
