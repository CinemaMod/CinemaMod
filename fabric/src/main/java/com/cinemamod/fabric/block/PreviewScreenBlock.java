package com.cinemamod.fabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class PreviewScreenBlock extends Block implements BlockEntityProvider {

    public static Identifier IDENT;
    public static PreviewScreenBlock PREVIEW_SCREEN_BLOCK;

    public PreviewScreenBlock() {
        super(FabricBlockSettings.create().solid().strength(-1.0f, 3600000.0F).dropsNothing().nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    public static void register() {
        IDENT = Identifier.of("cinemamod", "preview_screen");
        PREVIEW_SCREEN_BLOCK = new PreviewScreenBlock();

        Registry.register(Registries.BLOCK, IDENT, PREVIEW_SCREEN_BLOCK);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PreviewScreenBlockEntity(PreviewScreenBlockEntity.PREVIEW_SCREEN_BLOCK_ENTITY, pos, state);
    }

}
