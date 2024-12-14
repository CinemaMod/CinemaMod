package com.cinemamod.fabric.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ScreenBlock extends Block implements BlockEntityProvider {

    public static Identifier IDENT;
    public static ScreenBlock SCREEN_BLOCK;

    public ScreenBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    public static void register() {
        IDENT = Identifier.of("cinemamod", "screen_block");
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, IDENT);

        Settings settings = AbstractBlock.Settings.create().solid().strength(1.0f, 3600000.0F).dropsNothing().registryKey(registryKey);
        SCREEN_BLOCK = new ScreenBlock(settings);

        Registry.register(Registries.BLOCK, registryKey, SCREEN_BLOCK);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ScreenBlockEntity(ScreenBlockEntity.SCREEN_BLOCK_ENTITY, pos, state);
    }

}
