package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.tiledata.ControllerData;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import net.minecraft.block.state.IBlockState;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;

public class TileEntitySlave extends ChamTileEntity implements IDrawerGroup, IPriorityGroup, ISmartGroup
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
        return controllerCoord;
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public TileEntityController getController () {
        return controllerData.getController(this);
    }

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
    public IDrawer getDrawer (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.getDrawer(slot);
    }

    @Override
    public IDrawer getDrawerIfEnabled (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.getDrawerIfEnabled(slot);
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.isDrawerEnabled(slot);
    }

    @Override
    public void markDirty () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(getPos()))
            controller.markDirty();

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(getPos()))
            return controller.markDirtyIfNeeded();

        return false;
    }

    @Override
    public Iterable<Integer> enumerateDrawersForInsertion (ItemStack stack, boolean strict) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return new ArrayList<Integer>();

        return controller.enumerateDrawersForInsertion(stack, strict);
    }

    @Override
    public Iterable<Integer> enumerateDrawersForExtraction (ItemStack stack, boolean strict) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return new ArrayList<Integer>();

        return controller.enumerateDrawersForExtraction(stack, strict);
    }

    private DrawerItemHandler itemHandler = new DrawerItemHandler(this);

    @Override
    public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}
