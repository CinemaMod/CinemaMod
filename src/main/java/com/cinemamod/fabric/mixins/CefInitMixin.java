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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * A mixin is used here to load JCEF at the earliest point in the MC bootstrap process
 * See: net.minecraft.client.main.Main
 * This reduces issues with CEF initialization
 * Due to AWT issues on macOS, we cannot initialize CEF here
 */
@Mixin(Main.class)
public class CefInitMixin {

    private static void setUnixExecutable(File file) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);

        try {
            Files.setPosixFilePermissions(file.toPath(), perms);
        } catch (IOException e) {
            // Ignore
        }
    }

    private static void setupLibraryPath(Platform platform) throws IOException, URISyntaxException {
        // Check for development environment
        // i.e. cinemamod-repo/build/cef/<platform>
        File cefPlatformDir = new File("../build/cef/" + platform.getNormalizedName());
        if (cefPlatformDir.exists()) {
            System.setProperty("cinemamod.libraries.path", cefPlatformDir.getCanonicalPath());
            return;
        }

        // Check for .minecraft/mods/cinemamod-libraries directory, create if not exists
        File cinemaModLibrariesDir = new File("mods/cinemamod-libraries");
        if (!cinemaModLibrariesDir.exists()) {
            cinemaModLibrariesDir.mkdirs();
        }
        System.setProperty("cinemamod.libraries.path", cinemaModLibrariesDir.getCanonicalPath());

        //
        // CEF library extraction
        //
        URL cefManifestURL = CefInitMixin.class.getClassLoader().getResource("cef/cef_manifest.txt");

        if (cefManifestURL != null) {
            try (InputStream cefManifestInputStream = cefManifestURL.openStream();
                 Scanner scanner = new Scanner(cefManifestInputStream)) {
                while (scanner.hasNext()) {
                    String cefResourceName = scanner.nextLine();
                    URL cefResourceURL = CefInitMixin.class.getClassLoader().getResource("cef/" + cefResourceName);

                    if (cefResourceURL != null) {
                        try (InputStream cefResourceInputStream = cefResourceURL.openStream()) {
                            File cefResourceFile = new File(cinemaModLibrariesDir, cefResourceName);
                            if (!cefResourceFile.exists()) {
                                cefResourceFile.getParentFile().mkdirs(); // For when we run across a nested file, i.e. locales/sl.pak
                                Files.copy(cefResourceInputStream, cefResourceFile.toPath());
                                if (platform.isLinux()) {
                                    if (cefResourceFile.getName().contains("chrome-sandbox") || cefResourceFile.getName().contains("jcef_helper")) {
                                        setUnixExecutable(cefResourceFile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "main ([Ljava/lang/String;)V", remap = false)
    private static void cefInit(CallbackInfo info) {
        Platform platform = Platform.getPlatform();

        try {
            setupLibraryPath(platform);
        } catch (IOException | URISyntaxException e) {
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
