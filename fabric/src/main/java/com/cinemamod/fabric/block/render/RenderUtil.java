package com.cinemamod.fabric.block.render;

import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.util.math.MatrixStack;

public final class RenderUtil {

    public static void fixRotation(MatrixStack matrixStack, String facing) {
        final Quaternionf rotation;


        switch (facing) {
            case "NORTH":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(180));
                matrixStack.translate(0, 0, 1);
                break;
            case "WEST":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(-90.0));
                matrixStack.translate(0, 0, 0);
                break;
            case "EAST":
                rotation = new Quaternionf().rotationY((float) Math.toRadians(90.0));
                matrixStack.translate(-1, 0, 1);
                break;
            default:
                rotation = new Quaternionf();
                matrixStack.translate(-1, 0, 0);
                break;
        }
        matrixStack.multiply(rotation);
    }

    public static void moveForward(MatrixStack matrixStack, String facing, float amount) {
        switch (facing) {
            case "NORTH":
                matrixStack.translate(0, 0, -amount);
                break;
            case "WEST":
                matrixStack.translate(-amount, 0, 0);
                break;
            case "EAST":
                matrixStack.translate(amount, 0, 0);
                break;
            default:
                matrixStack.translate(0, 0, amount);
                break;
        }
    }

    public static void moveHorizontal(MatrixStack matrixStack, String facing, float amount) {
        switch (facing) {
            case "NORTH":
                matrixStack.translate(-amount, 0, 0);
                break;
            case "WEST":
                matrixStack.translate(0, 0, amount);
                break;
            case "EAST":
                matrixStack.translate(0, 0, -amount);
                break;
            default:
                matrixStack.translate(amount, 0, 0);
                break;
        }
    }

    public static void moveVertical(MatrixStack matrixStack, float amount) {
        matrixStack.translate(0, amount, 0);
    }

    public static void renderTexture(MatrixStack matrixStack, Tessellator tessellator, int glId) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, glId);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix4f, 0.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(0.0f, 1.0f);
        builder.vertex(matrix4f, 1.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(1.0f, 1.0f);
        builder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(255, 255, 255, 255).texture(1.0f, 0.0f);
        builder.vertex(matrix4f, 0, 0, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram(builder.end());
        RenderSystem.setShaderTexture(0, 0);
    }

    public static void renderColor(MatrixStack matrixStack, Tessellator tessellator, int r, int g, int b) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix4f, 0.0F, -1.0F, 1.0F).color(r, g, b, 255);
        builder.vertex(matrix4f, 1.0F, -1.0F, 1.0F).color(r, g, b, 255);
        builder.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(r, g, b, 255);
        builder.vertex(matrix4f, 0, 0, 0).color(r, g, b, 255);
        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    public static void renderBlack(MatrixStack matrixStack, Tessellator tessellator) {
        renderColor(matrixStack, tessellator, 0, 0, 0);
    }

}
