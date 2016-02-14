package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
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
                ItemStack stack = tableItemStacks[slot].splitStack(slot);
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
        return hasCustomInventoryName() ? customName : "container.storagedrawers.framingTable";
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
        return block instanceof BlockDrawersCustom;
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
}
