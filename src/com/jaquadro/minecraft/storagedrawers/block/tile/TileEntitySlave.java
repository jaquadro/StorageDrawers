package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntitySlave extends TileEntity implements ISidedInventory
{
    private BlockCoord controllerCoord;

    private int[] inventorySlots = new int[] { 0 };
    private int[] autoSides = new int[] { 0, 1, 2, 3, 4, 5 };

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("Controller", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ctag = tag.getCompoundTag("Controller");
            controllerCoord = new BlockCoord(ctag.getInteger("x"), ctag.getInteger("y"), ctag.getInteger("z"));
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (controllerCoord != null) {
            NBTTagCompound ctag = new NBTTagCompound();
            ctag.setInteger("x", controllerCoord.x());
            ctag.setInteger("y", controllerCoord.y());
            ctag.setInteger("z", controllerCoord.z());
            tag.setTag("Controller", ctag);
        }
    }

    @Override
    public Packet getDescriptionPacket () {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    private TileEntityController getController () {
        if (controllerCoord == null)
            return null;

        TileEntity te = worldObj.getTileEntity(controllerCoord.x(), controllerCoord.y(), controllerCoord.z());
        if (!(te instanceof TileEntityController)) {
            controllerCoord = null;
            markDirty();
            return null;
        }

        return (TileEntityController)te;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return inventorySlots;

        return controller.getAccessibleSlotsFromSide(0);
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return false;

        return controller.canInsertItem(slot, stack, 0);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return false;

        return controller.canExtractItem(slot, stack, side);
    }

    @Override
    public int getSizeInventory () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return 1;

        return controller.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return null;

        return controller.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return null;

        return controller.decrStackSize(slot, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return null;

        return controller.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return;

        controller.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName () {
        // TODO
        return null;
    }

    @Override
    public boolean hasCustomInventoryName () {
        // TODO
        return false;
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
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(xCoord, yCoord, zCoord))
            return false;

        return controller.isItemValidForSlot(slot, stack);
    }
}
