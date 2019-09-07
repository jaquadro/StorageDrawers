package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TileEntitySlave extends ChamTileEntity implements IDrawerGroup
{
    private static final int[] drawerSlots = new int[]{0};

    public final ControllerData controllerData = new ControllerData();

    public TileEntitySlave (TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        injectData(controllerData);
    }

    public TileEntitySlave () {
        this(ModBlocks.Tile.CONTROLLER_SLAVE);
    }

    public void bindController (BlockPos coord) {
        if (controllerData.bindCoord(coord))
            markDirty();
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
        if (controller == null || !controller.isValidSlave(getPos()))
            return drawerSlots;

        return controller.getAccessibleDrawerSlots();
    }

    @Override
    public int getDrawerCount () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return 0;

        return controller.getDrawerCount();
    }

    @Override
    @Nonnull
    public IDrawer getDrawer (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return Drawers.DISABLED;

        return controller.getDrawer(slot);
    }

    @Override
    public void markDirty () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(getPos()))
            controller.markDirty();

        super.markDirty();
    }

    @CapabilityInject(IItemHandler.class)
    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    @CapabilityInject(IItemRepository.class)
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;
    @CapabilityInject(IDrawerGroup.class)
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = null;

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
            if (controller == null || !controller.isValidSlave(getPos()))
                return NonNullList.create();

            return controller.getItemRepository().getAllItems();
        }

        @Nonnull
        @Override
        public ItemStack insertItem (@Nonnull ItemStack stack, boolean simulate, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getPos()))
                return stack;

            return controller.getItemRepository().insertItem(stack, simulate, predicate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem (@Nonnull ItemStack stack, int amount, boolean simulate, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getPos()))
                return ItemStack.EMPTY;

            return controller.getItemRepository().extractItem(stack, amount, simulate, predicate);
        }

        @Override
        public int getStoredItemCount (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getPos()))
                return 0;

            return controller.getItemRepository().getStoredItemCount(stack, predicate);
        }

        @Override
        public int getRemainingItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getPos()))
                return 0;

            return controller.getItemRepository().getRemainingItemCapacity(stack, predicate);
        }

        @Override
        public int getItemCapacity (@Nonnull ItemStack stack, Predicate<ItemStack> predicate) {
            TileEntityController controller = getController();
            if (controller == null || !controller.isValidSlave(getPos()))
                return 0;

            return controller.getItemRepository().getItemCapacity(stack, predicate);
        }
    }
}