package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileEntitySlave extends TileEntity implements IDrawerGroup, ISidedInventory
{
    private BlockPos controllerCoord;

    private int[] inventorySlots = new int[] { 0 };

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("Controller", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ctag = tag.getCompoundTag("Controller");
            controllerCoord = new BlockPos(ctag.getInteger("x"), ctag.getInteger("y"), ctag.getInteger("z"));
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (controllerCoord != null) {
            NBTTagCompound ctag = new NBTTagCompound();
            ctag.setInteger("x", controllerCoord.getX());
            ctag.setInteger("y", controllerCoord.getY());
            ctag.setInteger("z", controllerCoord.getZ());
            tag.setTag("Controller", ctag);
        }
    }

    @Override
    public Packet getDescriptionPacket () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(getPos(), 5, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        if (getWorld().isRemote)
            getWorld().markBlockForUpdate(getPos());
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
    public int[] getSlotsForFace (EnumFacing side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return inventorySlots;

        return controller.getSlotsForFace(EnumFacing.DOWN);
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, EnumFacing side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.canInsertItem(slot, stack, EnumFacing.DOWN);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, EnumFacing side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.canExtractItem(slot, stack, side);
    }

    @Override
    public int getSizeInventory () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return 1;

        return controller.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.decrStackSize(slot, count);
    }

    @Override
    public ItemStack removeStackFromSlot (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return;

        controller.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getName () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return "storageDrawers.container.unboundSlave";

        return controller.getName();
    }

    @Override
    public boolean hasCustomName () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.hasCustomName();
    }

    @Override
    public IChatComponent getDisplayName () {
        return null;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory (EntityPlayer player) {

    }

    @Override
    public void closeInventory (EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField (int id) {
        return 0;
    }

    @Override
    public void setField (int id, int value) {

    }

    @Override
    public int getFieldCount () {
        return 0;
    }

    @Override
    public void clear () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return;

        controller.clear();
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
    public boolean isDrawerEnabled (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return false;

        return controller.isDrawerEnabled(slot);
    }

    @Override
    public IDrawerInventory getDrawerInventory () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(getPos()))
            return null;

        return controller.getDrawerInventory();
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
}
