package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.api.storage.Drawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntitySlave extends ChamTileEntity implements IDrawerGroup
{
    private static final int[] drawerSlots = new int[] { 0 };

    public final ControllerData controllerData = new ControllerData();

    public TileEntitySlave () {
        injectData(controllerData);
    }

    public void bindController (BlockPos coord) {
        if (controllerData.bindCoord(coord))
            markDirty();
    }

    public BlockPos getControllerPos () {
        return controllerData.getCoord();
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
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

    private DrawerItemHandler itemHandler = new DrawerItemHandler(this);

    @Override
    public boolean hasCapability (@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability (@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}
