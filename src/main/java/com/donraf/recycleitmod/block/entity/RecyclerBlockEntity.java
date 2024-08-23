package com.donraf.recycleitmod.block.entity;

import com.donraf.recycleitmod.block.custom.RecyclerBlock;
import com.donraf.recycleitmod.item.ModItems;
import com.donraf.recycleitmod.screen.RecyclerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inputItemHandler = new ItemStackHandler(1);
    private final ItemStackHandler outputItemHandler = new ItemStackHandler(1);

    private final Random random = new Random();

    private LazyOptional<IItemHandler> lazyInputItemHandler = LazyOptional.of(() -> inputItemHandler);
    private LazyOptional<IItemHandler> lazyOutputItemHandler = LazyOptional.of(()-> outputItemHandler);

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 30;
    private float recycleProbability = 0.1f;

    public RecyclerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RECYCLER_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> RecyclerBlockEntity.this.progress;
                    case 1 -> RecyclerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> RecyclerBlockEntity.this.progress = pValue;
                    case 1 -> RecyclerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyInputItemHandler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            assert side != null;
            return switch (side) {
                case DOWN -> lazyOutputItemHandler.cast();
                default -> lazyInputItemHandler.cast();
            };
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInputItemHandler.invalidate();
        lazyOutputItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, inputItemHandler.getStackInSlot(0));
        inventory.setItem(1, outputItemHandler.getStackInSlot(0));
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.recycleitmod.recycler_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new RecyclerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("input_inventory", inputItemHandler.serializeNBT(pRegistries));
        pTag.put("output_inventory", outputItemHandler.serializeNBT(pRegistries));
        pTag.putInt("recycler.progress", progress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("input_inventory"));
        outputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("output_inventory"));
        progress = pTag.getInt("recycler.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (hasRecipe()) {
            pState = pState.setValue(RecyclerBlock.ON, true);
            pLevel.setBlock(pPos,pState, 3);
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            pState = pState.setValue(RecyclerBlock.ON, false);
            pLevel.setBlock(pPos,pState, 3);
            resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        this.inputItemHandler.extractItem(0, 1, false);
        if (random.nextFloat() <= recycleProbability){
            ItemStack result = new ItemStack(ModItems.SECONDARY_RAW_MATERIAL.get(), 1);
            this.outputItemHandler.setStackInSlot(0, new ItemStack(result.getItem(),
                    this.outputItemHandler.getStackInSlot(0).getCount() + result.getCount()));
        }
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.inputItemHandler.getStackInSlot(0).getCount() != 0;
        ItemStack result = new ItemStack(ModItems.SECONDARY_RAW_MATERIAL.get());

        return hasCraftingItem
                && canInsertAmountIntoOutputSlot(result.getCount())
                && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.outputItemHandler.getStackInSlot(0).isEmpty()
                || this.outputItemHandler.getStackInSlot(0).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.outputItemHandler.getStackInSlot(0).getCount() + count <= this.outputItemHandler.getStackInSlot(0).getMaxStackSize();
    }

}
