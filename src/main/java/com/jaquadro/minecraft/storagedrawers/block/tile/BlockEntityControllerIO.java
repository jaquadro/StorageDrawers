package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlockEntityControllerIO extends BaseBlockEntity implements IDrawerGroup
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

    static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    static Capability<IDrawerGroup> DRAWER_GROUP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final DrawerItemHandler itemHandler = new DrawerItemHandler(this);
    private final ItemRepositoryProxy itemRepository = new ItemRepositoryProxy();

    private final LazyOptional<IItemHandler> capabilityItemHandler = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IItemRepository> capabilityItemRepository = LazyOptional.of(() -> itemRepository);
    private final LazyOptional<IDrawerGroup> capabilityGroup = LazyOptional.of(() -> this);

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability (@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return capabilityItemHandler.cast();
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return capabilityItemRepository.cast();
        if (capability == DRAWER_GROUP_CAPABILITY)
            return capabilityGroup.cast();

        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capabilityItemHandler.invalidate();
        capabilityItemRepository.invalidate();
        capabilityGroup.invalidate();
    }

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