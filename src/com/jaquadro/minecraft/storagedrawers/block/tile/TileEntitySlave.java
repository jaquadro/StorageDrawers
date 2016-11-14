package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.inventory.DrawerItemHandler;
import net.minecraft.block.state.IBlockState;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntitySlave extends TileEntity implements IDrawerGroup, IPriorityGroup
{
    private BlockPos controllerCoord;

    private int[] inventorySlots = new int[] { 0 };
    private int[] drawerSlots = new int[] { 0 };

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("Controller", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ctag = tag.getCompoundTag("Controller");
            controllerCoord = new BlockPos(ctag.getInteger("x"), ctag.getInteger("y"), ctag.getInteger("z"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (controllerCoord != null) {
            NBTTagCompound ctag = new NBTTagCompound();
            ctag.setInteger("x", controllerCoord.getX());
            ctag.setInteger("y", controllerCoord.getY());
            ctag.setInteger("z", controllerCoord.getZ());
            tag.setTag("Controller", ctag);
        }

        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket () {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (getWorld().isRemote) {
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public void bindController (BlockPos coord) {
        if (controllerCoord != null && controllerCoord.equals(getPos()))
            return;

        controllerCoord = coord;
        markDirty();
    }

    @Override
    public boolean shouldRefresh (World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public TileEntityController getController () {
        if (controllerCoord == null)
            return null;

        TileEntity te = worldObj.getTileEntity(controllerCoord);
        if (!(te instanceof TileEntityController)) {
            controllerCoord = null;
            markDirty();
            return null;
        }

        return (TileEntityController)te;
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
