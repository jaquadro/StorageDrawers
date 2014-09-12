package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityDrawersBase extends TileEntity
{
    private int direction;
    private int drawerCapacity = 1;
    private int level = 1;

    protected int[] autoSides = new int[] { 0, 1 };

    public int getDirection () {
        return direction;
    }

    public void setDirection (int direction) {
        this.direction = direction % 6;
        autoSides = new int[] { 0, 1, ForgeDirection.OPPOSITES[direction] };
    }

    public int getLevel () {
        return level;
    }

    public void setLevel (int level) {
        this.level = MathHelper.clamp_int(level, 1, 6);
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

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        direction = tag.getByte("Dir");
        drawerCapacity = tag.getByte("Cap");
        level = tag.getByte("Lev");

        autoSides = new int[] { 0, 1, ForgeDirection.OPPOSITES[direction] };
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("Dir", (byte)direction);
        tag.setByte("Cap", (byte)drawerCapacity);
        tag.setByte("Lev", (byte)level);
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
}