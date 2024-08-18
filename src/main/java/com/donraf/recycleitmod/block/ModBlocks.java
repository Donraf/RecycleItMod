package com.donraf.recycleitmod.block;

import com.donraf.recycleitmod.RecycleItMod;
import com.donraf.recycleitmod.block.custom.RecyclerBlock;
import com.donraf.recycleitmod.block.custom.SynthesizerBlock;
import com.donraf.recycleitmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RecycleItMod.MOD_ID);

    public static final RegistryObject<Block> RECYCLER_BLOCK = regsisterBlock("recycler_block",
            () -> new RecyclerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> SYNTHESIZER_BLOCK = regsisterBlock("synthesizer_block",
            () -> new SynthesizerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    private static <T extends Block> RegistryObject<T> regsisterBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
