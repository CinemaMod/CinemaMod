package com.cinemamod.fabric.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ScreenBlockEntity extends BlockEntity {

    public static Identifier IDENT;
    public static BlockEntityType<ScreenBlockEntity> SCREEN_BLOCK_ENTITY;

    public ScreenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ScreenBlockEntity(BlockPos pos, BlockState state) {
        super(SCREEN_BLOCK_ENTITY, pos, state);
    }

    public static void register() {
        IDENT = Identifier.of("cinemamod", "screen_block_entity");
        SCREEN_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
                .create(ScreenBlockEntity::new, ScreenBlock.SCREEN_BLOCK)
                .build();
        Registry.register(Registries.BLOCK_ENTITY_TYPE, IDENT, SCREEN_BLOCK_ENTITY);
    }

}
