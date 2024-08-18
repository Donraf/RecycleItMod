package com.donraf.recycleitmod.block.entity;

import com.donraf.recycleitmod.RecycleItMod;
import com.donraf.recycleitmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RecycleItMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<RecyclerBlockEntity>> RECYCLER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("recycler_block_entity", () ->
                    BlockEntityType.Builder.of(RecyclerBlockEntity::new,
                            ModBlocks.RECYCLER_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<SynthesizerBlockEntity>> SYNTHESIZER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("synthesizer_block_entity", () ->
                    BlockEntityType.Builder.of(SynthesizerBlockEntity::new,
                            ModBlocks.SYNTHESIZER_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
