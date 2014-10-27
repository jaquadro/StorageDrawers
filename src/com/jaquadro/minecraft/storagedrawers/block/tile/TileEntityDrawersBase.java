package com.jaquadro.minecraft.storagedrawers.block.tile;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityDrawersBase extends TileEntity implements ISidedInventory
{
    private int direction;
    private int drawerCapacity = 1;
    private int level = 1;
    private int statusLevel = 0;
    private ItemStack[] upgrades = new ItemStack[5];

    protected int[] autoSides = new int[] { 0, 1 };

    public int getDirection () {
        return direction;
    }

    public void setDirection (int direction) {
        this.direction = direction % 6;
        autoSides = new int[] { 0, 1, ForgeDirection.OPPOSITES[direction], 2, 3 };

        if (direction == 2 || direction == 3) {
            autoSides[3] = 4;
            autoSides[4] = 5;
        }
    }

    public int getLevel () {
        return level;
    }

    public void setLevel (int level) {
        this.level = MathHelper.clamp_int(level, 1, 6);
    }

    public int getStatusLevel () {
        return statusLevel;
    }

    public void setStatusLevel (int level) {
        this.statusLevel = MathHelper.clamp_int(level, 1, 3);
    }

    public int getDrawerCapacity () {
        return drawerCapacity;
    }

    public void setDrawerCapacity (int stackCount) {
        drawerCapacity = stackCount;
    }

    public abstract int getDrawerCount ();

    public abstract ItemStack takeItemsFromSlot (int slot, int count);

    public abstract int interactPutItemsIntoSlot (int slot, EntityPlayer player);

    public abstract int getItemCount (int slot);

    public abstract int getItemStackSize (int slot);

    public abstract int getItemCapacity (int slot);

    public abstract ItemStack getSingleItemStack (int slot);

    public ItemStack getUpgrade (int slot) {
        if (slot < 0 || slot >= upgrades.length)
            return null;

        return upgrades[slot];
    }

    public void setUpgrade (int slot, ItemStack upgrade) {
        if (slot < 0 || slot >= upgrades.length)
            return;

        upgrades[slot] = upgrade;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        setDirection(tag.getByte("Dir"));

        drawerCapacity = tag.getByte("Cap");
        level = tag.getByte("Lev");

        statusLevel = 0;
        if (tag.hasKey("Stat"))
            statusLevel = tag.getByte("Stat");

        for (int i = 0, n = upgrades.length; i < n; i++)
            upgrades[i] = null;

        if (tag.hasKey("Upgrades")) {
            NBTTagList list = tag.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
            for (int i = 0, n = list.tagCount(); i < n; i++) {
                NBTTagCompound uTag = list.getCompoundTagAt(i);

                ItemStack item = new ItemStack(Item.getItemById(uTag.getShort("Item")), 1, uTag.getShort("Meta"));
                upgrades[uTag.getByte("Slot")] = item;
            }
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("Dir", (byte)direction);
        tag.setByte("Cap", (byte)drawerCapacity);
        tag.setByte("Lev", (byte)level);

        if (statusLevel > 0)
            tag.setByte("Stat", (byte)statusLevel);

        if (getUpgradeCount() > 0) {
            NBTTagList list = new NBTTagList();

            for (int i = 0, n = upgrades.length; i < n; i++) {
                if (upgrades[i] == null)
                    continue;

                NBTTagCompound uTag = new NBTTagCompound();
                uTag.setByte("Slot", (byte)i);
                uTag.setShort("Item", (short) Item.getIdFromItem(upgrades[i].getItem()));
                uTag.setShort("Meta", (short) upgrades[i].getItemDamage());

                list.appendTag(uTag);
            }

            tag.setTag("Upgrades", list);
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

    private int getUpgradeCount () {
        int count = 0;
        for (ItemStack item : upgrades)
            count += (item != null) ? 1 : 0;

        return count;
    }

    public abstract void clientUpdateCount (int slot, int count);
}
