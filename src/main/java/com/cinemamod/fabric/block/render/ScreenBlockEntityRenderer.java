package com.cinemamod.fabric.block.render;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.block.ScreenBlockEntity;
import com.cinemamod.fabric.screen.Screen;
import com.cinemamod.fabric.screen.ScreenManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenBlockEntityRenderer implements BlockEntityRenderer<ScreenBlockEntity> {

    public ScreenBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ScreenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ScreenManager screenManager = CinemaModClient.getInstance().getScreenManager();
        Screen screen = screenManager.getScreen(entity.getPos());
        if (screen == null || !screen.isVisible()) return;
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        renderScreenTexture(screen, matrices, tessellator, buffer);
        RenderSystem.disableDepthTest();
    }

    private static void renderScreenTexture(Screen screen, MatrixStack matrices, Tessellator tessellator, BufferBuilder buffer) {
        matrices.push();
        matrices.translate(1, 1, 0);
        RenderUtil.moveForward(matrices, screen.getFacing(), 0.008f);
        RenderUtil.fixRotation(matrices, screen.getFacing());
        matrices.scale(screen.getWidth(), screen.getHeight(), 0);
        if (screen.hasBrowser()) {
            int glId = screen.getBrowser().renderer.texture_id_[0];
            RenderUtil.renderTexture(matrices, tessellator, buffer, glId);
        } else {
            RenderUtil.renderBlack(matrices, tessellator, buffer);
        }
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(ScreenBlockEntity blockEntity) {
        return true;
    }

    public static void register() {
        BlockEntityRendererRegistry.register(ScreenBlockEntity.SCREEN_BLOCK_ENTITY, ScreenBlockEntityRenderer::new);
    }

}
