package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntitySlave extends TileEntity implements IDrawerGroup, IPriorityGroup, ISmartGroup, ISidedInventory
{
    private BlockCoord controllerCoord;
    private BlockCoord selfCoord;

    private int[] inventorySlots = new int[] { 0 };
    private int[] drawerSlots = new int[] { 0 };

    public void ensureInitialized () {
        if (selfCoord == null) {
            selfCoord = new BlockCoord(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        selfCoord = new BlockCoord(xCoord, yCoord, zCoord);

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

    public void bindController (int x, int y, int z) {
        if (controllerCoord != null && controllerCoord.x() == x && controllerCoord.y() == y && controllerCoord.z() == z)
            return;

        controllerCoord = new BlockCoord(x, y, z);
        markDirty();
    }

    public TileEntityController getController () {
        if (controllerCoord == null)
            return null;

        ensureInitialized();

        TileEntity te = worldObj.getTileEntity(controllerCoord.x(), controllerCoord.y(), controllerCoord.z());
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
        if (controller == null || !controller.isValidSlave(selfCoord))
            return drawerSlots;

        return controller.getAccessibleDrawerSlots();
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return inventorySlots;

        return controller.getAccessibleSlotsFromSide(0);
    }

    @Override
    public boolean canInsertItem (int slot, ItemStack stack, int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return false;

        return controller.canInsertItem(slot, stack, 0);
    }

    @Override
    public boolean canExtractItem (int slot, ItemStack stack, int side) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return false;

        return controller.canExtractItem(slot, stack, side);
    }

    @Override
    public int getSizeInventory () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return 1;

        return controller.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.decrStackSize(slot, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return;

        controller.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return "storageDrawers.container.unboundSlave";

        return controller.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return false;

        return controller.hasCustomInventoryName();
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
        if (controller == null || !controller.isValidSlave(selfCoord))
            return false;

        return controller.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getDrawerCount () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return 0;

        return controller.getDrawerCount();
    }

    @Override
    public IDrawer getDrawer (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.getDrawer(slot);
    }

    @Override
    public boolean isDrawerEnabled (int slot) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return false;

        return controller.isDrawerEnabled(slot);
    }

    @Override
    public IDrawerInventory getDrawerInventory () {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.getDrawerInventory();
    }

    @Override
    public Iterable<Integer> enumerateDrawersForInsertion (ItemStack stack, boolean strict) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.enumerateDrawersForInsertion(stack, strict);
    }

    @Override
    public Iterable<Integer> enumerateDrawersForExtraction (ItemStack stack, boolean strict) {
        TileEntityController controller = getController();
        if (controller == null || !controller.isValidSlave(selfCoord))
            return null;

        return controller.enumerateDrawersForExtraction(stack, strict);
    }

    @Override
    public void markDirty () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(selfCoord))
            controller.markDirty();

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded () {
        TileEntityController controller = getController();
        if (controller != null && controller.isValidSlave(selfCoord))
            return controller.markDirtyIfNeeded();

        return false;
    }
}
