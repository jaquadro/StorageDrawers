package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityTrim extends ChamTileEntity
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
    public void readFromPortableNBT (NBTTagCompound tag) {
        super.readFromPortableNBT(tag);

        materialSide = null;
        if (tag.hasKey("MatS"))
            materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));

        materialTrim = null;
        if (tag.hasKey("MatT"))
            materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        tag = super.writeToPortableNBT(tag);

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
    public boolean dataPacketRequiresRenderUpdate () {
        return true;
    }
}
