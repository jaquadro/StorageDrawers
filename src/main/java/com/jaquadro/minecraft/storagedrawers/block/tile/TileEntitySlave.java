package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository.ItemRecord;

public class TileEntitySlave extends ChamTileEntity implements IDrawerGroup
{
    private static final int[] drawerSlots = new int[]{0};

    public final ControllerData controllerData = new ControllerData();

    public TileEntitySlave (BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);

        injectData(controllerData);
    }

    public TileEntitySlave (BlockPos pos, BlockState state) {
        this(ModBlocks.Tile.CONTROLLER_SLAVE, pos, state);
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

    public TileEntityController getController () {
        return controllerData.getController(this);
    }

    @Nonnull
    @Override
    public int[] getAccessibleDrawerSlots () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getBlockPos()))
            return drawerSlots;

        return controller.getAccessibleDrawerSlots();
    }

    @Override
    public int getDrawerCount () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getBlockPos()))
            return 0;

        return controller.getDrawerCount();
    }

    @Override
    @Nonnull
    public IDrawer getDrawer (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getBlockPos()))
            return Drawers.DISABLED;

        return controller.getDrawer(slot);
    }

    @Override
    public void setChanged () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(getBlockPos()))
            controller.setChanged();

        super.setChanged();
    }

    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final DrawerItemHandler itemHandler = new DrawerItemHandler(this);
    private final ItemRepositoryProxy itemRepository = new ItemRepositoryProxy();

    private final LazyOptional<?> capabilityItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<?> capabilityItemRepository = LazyOptional.of(() -> itemRepository);
    private final LazyOptional<?> capabilityGroup = LazyOptional.of(() -> this);

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return capabilityItemHandler.cast();
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return capabilityItemRepository.cast();
        if (capability == DRAWER_GROUP_CAPABILITY)
            return capabilityGroup.cast();

        return super.getCapability(capability, facing);
    }

    private class ItemRepositoryProxy implements IItemRepository
    {
        @Nonnull
        @Override
        public NonNullList<ItemRecord> getAllItems () {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return NonNullList.create();

            return controller.getItemRepository().getAllItems();
        }

        @Nonnull
        @Override
        public ItemStack insertItem (@Nonnull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return stack;

            return controller.getItemRepository().insertItem(stack, simulate, predicate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem (@Nonnull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return ItemStack.EMPTY;

            return controller.getItemRepository().extractItem(stack, amount, simulate, predicate);
        }

        @Override
        public int getStoredItemCount (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return 0;

            return controller.getItemRepository().getStoredItemCount(stack, predicate);
        }

        @Override
        public int getRemainingItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return 0;

            return controller.getItemRepository().getRemainingItemCapacity(stack, predicate);
        }

        @Override
        public int getItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getBlockPos()))
                return 0;

            return controller.getItemRepository().getItemCapacity(stack, predicate);
        }
    }
}