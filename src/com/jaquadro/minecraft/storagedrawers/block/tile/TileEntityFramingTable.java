package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.BlockTrimCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

public class TileEntityFramingTable extends TileEntity implements IInventory
{
    private ItemStack[] tableItemStacks = new ItemStack[5];

    private String customName;

    @Override
    public int getSizeInventory () {
        return tableItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return tableItemStacks[slot];
    }

    @Override
    public ItemStack decrStackSize (int slot, int count) {
        if (tableItemStacks[slot] != null) {
            if (tableItemStacks[slot].stackSize <= count) {
                ItemStack stack = tableItemStacks[slot];
                tableItemStacks[slot] = null;
                markDirty();
                return stack;
            }
            else {
                ItemStack stack = tableItemStacks[slot].splitStack(count);
                if (tableItemStacks[slot].stackSize == 0)
                    tableItemStacks[slot] = null;

                markDirty();
                return stack;
            }
        }
        else
            return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {
        tableItemStacks[slot] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();

        markDirty();
    }

    @Override
    public String getInventoryName () {
        return hasCustomInventoryName() ? customName : "storageDrawers.container.framingTable";
    }

    @Override
    public boolean hasCustomInventoryName () {
        return customName != null && customName.length() > 0;
    }

    public void setCustomName (String name) {
        customName = name;
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer player) {
        if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
            return false;

        return player.getDistanceSq(xCoord + .5, yCoord + .5, zCoord + .5) <= 64;
    }

    @Override
    public void openInventory () {

    }

    @Override
    public void closeInventory () {

    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack stack) {
        if (slot == 0)
            return isItemValidDrawer(stack);
        if (slot == 4)
            return false;
        if (slot >= 1 && slot < 4)
            return isItemValidMaterial(stack);

        return false;
    }

    public static boolean isItemValidDrawer (ItemStack stack) {
        if (stack == null)
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());
        return block instanceof BlockDrawersCustom || block instanceof BlockTrimCustom;
    }

    public static boolean isItemValidMaterial (ItemStack stack) {
        if (stack == null)
            return false;

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null)
            return false;

        return block.isOpaqueCube();
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        NBTTagList itemList = tag.getTagList("Items", 10);
        tableItemStacks = new ItemStack[getSizeInventory()];

        for (int i = 0; i < itemList.tagCount(); i++) {
            NBTTagCompound item = itemList.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");

            if (slot >= 0 && slot < tableItemStacks.length)
                tableItemStacks[slot] = ItemStack.loadItemStackFromNBT(item);
        }

        if (tag.hasKey("CustomName", Constants.NBT.TAG_STRING))
            customName = tag.getString("CustomName");
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < tableItemStacks.length; i++) {
            if (tableItemStacks[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte)i);
                tableItemStacks[i].writeToNBT(item);
                itemList.appendTag(item);
            }
        }

        tag.setTag("Items", itemList);

        if (hasCustomInventoryName())
            tag.setString("CustomName", customName);
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
        //getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    private static final AxisAlignedBB ZERO_EXTENT_AABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

    @Override
    public AxisAlignedBB getRenderBoundingBox () {
        int meta = getBlockMetadata();
        if ((meta & 0x8) != 0)
            return ZERO_EXTENT_AABB;

        int side = meta & 0x7;
        int xOff = 0;
        int zOff = 0;

        if (side == 2)
            xOff = 1;
        if (side == 3)
            xOff = -1;
        if (side == 4)
            zOff = -1;
        if (side == 5)
            zOff = 1;

        int xMin = Math.min(xCoord, xCoord + xOff);
        int xMax = Math.max(xCoord, xCoord + xOff) + 1;
        int zMin = Math.min(zCoord, zCoord + zOff);
        int zMax = Math.max(zCoord, zCoord + zOff) + 1;
        return AxisAlignedBB.getBoundingBox(xMin, yCoord + 1, xMax, zMin, yCoord + 2, zMax);
    }
}
