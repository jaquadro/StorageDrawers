package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.mojang.authlib.GameProfile;
import com.texelsaurus.minecraft.chameleon.capabilities.ChameleonCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BlockEntityControllerIO extends BaseBlockEntity implements IDrawerGroup, IControlGroup
{
    private static final int[] drawerSlots = new int[]{0};

    public final ControllerData controllerData = new ControllerData();

    public BlockEntityControllerIO (BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);

        injectData(controllerData);
    }

    public BlockEntityControllerIO (BlockPos pos, BlockState state) {
        this(ModBlockEntities.CONTROLLER_IO.get(), pos, state);
    }

    @Override
    public IDrawerGroup getDrawerGroup () {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return null;

        return controller.getDrawerGroup();
    }

    @Override
    public IDrawerAttributesGroupControl getGroupControllableAttributes (GameProfile profile) {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return null;

        return controller.getGroupControllableAttributes(profile);
    }

    @Override
    public IControlGroup getBoundControlGroup () {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return null;

        return controller;
    }

    @Override
    public boolean isGroupValid () {
        return !isRemoved();
    }

    public void bindController (BlockPos coord) {
        if (controllerData.bindCoord(coord))
            setChanged();
    }

    public BlockPos getControllerPos () {
        return controllerData.getCoord();
    }

    public BlockEntityController getController () {
        return controllerData.getController(this);
    }

    public IItemRepository getItemRepository () {
        return itemRepository;
    }

    @Override
    public int[] getAccessibleDrawerSlots () {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return drawerSlots;

        return controller.getAccessibleDrawerSlots();
    }

    @Override
    public int getDrawerCount () {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return 0;

        return controller.getDrawerCount();
    }

    @Override
    @NotNull
    public IDrawer getDrawer (int slot) {
        BlockEntityController controller = getController();
        if (controller == null || !controller.isValidIO(getBlockPos()))
            return Drawers.DISABLED;

        return controller.getDrawer(slot);
    }

    @Override
    public void setChanged () {
        BlockEntityController controller = getController();
        if (controller != null && controller.isValidIO(getBlockPos()))
            controller.setChanged();

        super.setChanged();
    }

    @Override
    public <T> T getCapability (ChameleonCapability<T> capability) {
        if (capability == null || level == null)
            return null;
        return capability.getCapability(level, getBlockPos());
    }

    /*
    public <T> T getCapability(@NotNull BlockCapability<T, Void> capability) {
        if (level == null)
            return null;
        return level.getCapability(capability, getBlockPos(), getBlockState(), this, null);
    }*/

    private final ItemRepositoryProxy itemRepository = new ItemRepositoryProxy();

    private class ItemRepositoryProxy implements IItemRepository
    {
        @NotNull
        @Override
        public NonNullList<ItemRecord> getAllItems () {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return NonNullList.create();

            return controller.getItemRepository().getAllItems();
        }

        @NotNull
        @Override
        public ItemStack insertItem (@NotNull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return stack;

            return controller.getItemRepository().insertItem(stack, simulate, predicate);
        }

        @NotNull
        @Override
        public ItemStack extractItem (@NotNull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return ItemStack.EMPTY;

            return controller.getItemRepository().extractItem(stack, amount, simulate, predicate);
        }

        @Override
        public int getStoredItemCount (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return 0;

            return controller.getItemRepository().getStoredItemCount(stack, predicate);
        }

        @Override
        public int getRemainingItemCapacity (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return 0;

            return controller.getItemRepository().getRemainingItemCapacity(stack, predicate);
        }

        @Override
        public int getItemCapacity (@NotNull ItemStack stack, Predicate<ItemStack> predicate) {
            BlockEntityController controller = getController();
            if (controller == null || !controller.isValidIO(getBlockPos()))
                return 0;

            return controller.getItemRepository().getItemCapacity(stack, predicate);
        }
    }
}