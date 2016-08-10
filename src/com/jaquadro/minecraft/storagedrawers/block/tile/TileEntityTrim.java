package com.jaquadro.minecraft.storagedrawers.block.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTrim extends TileEntity
{
    private ItemStack materialSide;
    private ItemStack materialTrim;

    public ItemStack getMaterialSide () {
        return materialSide;
    }

    public ItemStack getMaterialTrim () {
        return materialTrim;
    }

    public ItemStack getEffectiveMaterialSide () {
        return materialSide;
    }

    public ItemStack getEffectiveMaterialTrim () {
        return materialTrim != null ? materialTrim : materialSide;
    }

    public void setMaterialSide (ItemStack material) {
        materialSide = material;
    }

    public void setMaterialTrim (ItemStack material) {
        materialTrim = material;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        super.readFromNBT(tag);

        materialSide = null;
        if (tag.hasKey("MatS"))
            materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));

        materialTrim = null;
        if (tag.hasKey("MatT"))
            materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (materialSide != null) {
            NBTTagCompound itag = new NBTTagCompound();
            materialSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (materialTrim != null) {
            NBTTagCompound itag = new NBTTagCompound();
            materialTrim.writeToNBT(itag);
            tag.setTag("MatT", itag);
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
}
