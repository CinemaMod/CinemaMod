package com.cinemamod.block.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class RenderUtil {
    public static void fixRotation(PoseStack poseStack, String facing) {
        final Quaternionf rotation;


        switch (facing) {
            case "NORTH":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(180));
                poseStack.translate(0, 0, 1);
                break;
            case "WEST":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(-90.0));
                poseStack.translate(0, 0, 0);
                break;
            case "EAST":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(90.0));
                poseStack.translate(-1, 0, 1);
                break;
            default:
                rotation = new Quaternionf();
                poseStack.translate(-1, 0, 0);
                break;
        }
        matrixStack.multiply(rotation);
    }

    public static void moveForward(PoseStack poseStack, String facing, float amount) {
        switch (facing) {
            case "NORTH":
                poseStack.translate(0, 0, -amount);
                break;
            case "WEST":
                poseStack.translate(-amount, 0, 0);
                break;
            case "EAST":
                poseStack.translate(amount, 0, 0);
                break;
            default:
                poseStack.translate(0, 0, amount);
                break;
        }
    }

    public static void moveHorizontal(PoseStack poseStack, String facing, float amount) {
        switch (facing) {
            case "NORTH":
                poseStack.translate(-amount, 0, 0);
                break;
            case "WEST":
                poseStack.translate(0, 0, amount);
                break;
            case "EAST":
                poseStack.translate(0, 0, -amount);
                break;
            default:
                poseStack.translate(amount, 0, 0);
                break;
        }
    }

    public static void moveVertical(PoseStack poseStack, float amount) {
        poseStack.translate(0, amount, 0);
    }

    public static void renderTexture(PoseStack poseStack, Tessellator tessellator, BufferBuilder buffer, int glId) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, glId);
        Matrix4f matrix4f = poseStack.peek().getPositionMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(matrix4f, 0.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(0.0f, 1.0f).next();
        buffer.vertex(matrix4f, 1.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(1.0f, 1.0f).next();
        buffer.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(255, 255, 255, 255).texture(1.0f, 0.0f).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.setShaderTexture(0, 0);
    }

    public static void renderColor(PoseStack poseStack, Tessellator tessellator, BufferBuilder buffer, int r, int g, int b) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix4f, 0.0F, -1.0F, 1.0F).color(r, g, b, 255).next();
        buffer.vertex(matrix4f, 1.0F, -1.0F, 1.0F).color(r, g, b, 255).next();
        buffer.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(r, g, b, 255).next();
        buffer.vertex(matrix4f, 0, 0, 0).color(r, g, b, 255).next();
        tessellator.draw();
    }

    public static void renderBlack(PoseStack poseStack, Tessellator tessellator, BufferBuilder buffer) {
        renderColor(matrixStack, tessellator, buffer, 0, 0, 0);
    }
}
