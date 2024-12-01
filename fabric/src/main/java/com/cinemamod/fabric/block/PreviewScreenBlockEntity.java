package com.cinemamod.fabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PreviewScreenBlockEntity extends BlockEntity {

    public static Identifier IDENT;
    public static BlockEntityType<PreviewScreenBlockEntity> PREVIEW_SCREEN_BLOCK_ENTITY;

    public PreviewScreenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public PreviewScreenBlockEntity(BlockPos pos, BlockState state) {
        super(PREVIEW_SCREEN_BLOCK_ENTITY, pos, state);
    }

    public static void register() {
        IDENT = Identifier.of("cinemamod", "preview_screen_block_entity");
        PREVIEW_SCREEN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
                .create(PreviewScreenBlockEntity::new, PreviewScreenBlock.PREVIEW_SCREEN_BLOCK)
                .build();
        Registry.register(Registries.BLOCK_ENTITY_TYPE, IDENT, PREVIEW_SCREEN_BLOCK_ENTITY);
    }

}
