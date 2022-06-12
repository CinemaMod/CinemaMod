package com.cinemamod.fabric.block.render;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.block.PreviewScreenBlockEntity;
import com.cinemamod.fabric.screen.PreviewScreen;
import com.cinemamod.fabric.screen.PreviewScreenManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

public class PreviewScreenBlockEntityRenderer implements BlockEntityRenderer<PreviewScreenBlockEntity> {

    public PreviewScreenBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(PreviewScreenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        PreviewScreenManager previewScreenManager = CinemaModClient.getInstance().getPreviewScreenManager();
        PreviewScreen previewScreen = previewScreenManager.getPreviewScreen(entity.getPos());
        if (previewScreen == null) return;
        RenderSystem.enableDepthTest();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        renderScreenTexture(previewScreen, matrices, tessellator, buffer);
        renderVideoThumbnail(previewScreen, matrices, tessellator, buffer);
        renderScreenText(previewScreen, matrices);
        RenderSystem.disableDepthTest();
    }

    private static void renderScreenTexture(PreviewScreen previewScreen, MatrixStack matrices, Tessellator tessellator, BufferBuilder buffer) {
        NativeImageBackedTexture texture = previewScreen.hasVideoInfo() ? previewScreen.getActiveTexture() : previewScreen.getStaticTexture();

        if (texture != null) {
            matrices.push();
            matrices.translate(1, 1, 0);
            RenderUtil.moveForward(matrices, previewScreen.getFacing(), 0.008f);
            RenderUtil.fixRotation(matrices, previewScreen.getFacing());
            matrices.scale(3, 2, 0);
            RenderUtil.renderTexture(matrices, tessellator, buffer, texture.getGlId());
            matrices.pop();
        }
    }

    private static void renderVideoThumbnail(PreviewScreen previewScreen, MatrixStack matrices, Tessellator tessellator, BufferBuilder buffer) {
        NativeImageBackedTexture texture = previewScreen.getThumbnailTexture();

        if (texture != null) {
            matrices.push();
            matrices.translate(1, 1, 0);
            RenderUtil.moveHorizontal(matrices, previewScreen.getFacing(), 0.5f);
            RenderUtil.moveVertical(matrices, -1 / 3f);
            RenderUtil.moveForward(matrices, previewScreen.getFacing(), 0.01f);
            RenderUtil.fixRotation(matrices, previewScreen.getFacing());
            matrices.scale(3 / 1.5f, 2 / 1.5f, 0);
            RenderUtil.renderTexture(matrices, tessellator, buffer, texture.getGlId());
            matrices.pop();
        }
    }

    private static void renderScreenText(PreviewScreen previewScreen, MatrixStack matrices) {
        matrices.push();
        matrices.translate(1, 1, 0);
        RenderUtil.moveHorizontal(matrices, previewScreen.getFacing(), 0.1f);
        RenderUtil.moveVertical(matrices, -0.15f);
        RenderUtil.moveForward(matrices, previewScreen.getFacing(), 0.01f);
        RenderUtil.fixRotation(matrices, previewScreen.getFacing());
        matrices.multiply(new Quaternion(180, 0, 0, true));
        matrices.scale(0.02f, 0.02f, 0.02f);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final String topText;
        final String bottomText;
        if (previewScreen.hasVideoInfo()) {
            topText = previewScreen.getVideoInfo().getTitleShort();
            bottomText = previewScreen.getVideoInfo().getPoster();
        } else {
            topText = "NOTHING PLAYING";
            bottomText = "";
        }
        textRenderer.draw(matrices, topText, 0F, 0F, 16777215);
        RenderUtil.moveVertical(matrices, 78f);
        textRenderer.draw(matrices, bottomText, 0F, 0F, 16777215);
        matrices.pop();
    }

    public static void register() {
        BlockEntityRendererRegistry.register(PreviewScreenBlockEntity.PREVIEW_SCREEN_BLOCK_ENTITY, PreviewScreenBlockEntityRenderer::new);
    }

}
