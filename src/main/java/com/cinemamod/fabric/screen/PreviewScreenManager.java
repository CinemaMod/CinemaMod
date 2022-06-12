package com.cinemamod.fabric.screen;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class PreviewScreenManager {

    private Map<BlockPos, PreviewScreen> previewScreens;

    public PreviewScreenManager() {
        previewScreens = new HashMap<>();
    }

    public void addPreviewScreen(PreviewScreen previewScreen) {
        if (previewScreens.containsKey(previewScreen.getBlockPos())) {
            previewScreens.remove(previewScreen.getBlockPos()).unregister();
        }

        previewScreen.register();

        previewScreens.put(previewScreen.getBlockPos(), previewScreen);
    }

    public PreviewScreen getPreviewScreen(BlockPos blockPos) {
        return previewScreens.get(blockPos);
    }

    public void unloadAll() {
        previewScreens.values().forEach(PreviewScreen::unregister);
        previewScreens.clear();
    }

}
