package com.jaquadro.minecraft.storagedrawers.block.tile;

import com.jaquadro.minecraft.chameleon.block.ChamTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class TileEntityTrim extends ChamTileEntity
{
    @Nonnull
    private ItemStack materialSide;
    @Nonnull
    private ItemStack materialTrim;

    public TileEntityTrim () {
        materialSide = ItemStack.field_190927_a;
        materialTrim = ItemStack.field_190927_a;
    }

    @Nonnull
    public ItemStack getMaterialSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getMaterialTrim () {
        return materialTrim;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialTrim () {
        return !materialTrim.func_190926_b() ? materialTrim : materialSide;
    }

    public void setMaterialSide (@Nonnull ItemStack material) {
        materialSide = material;
    }

    public void setMaterialTrim (@Nonnull ItemStack material) {
        materialTrim = material;
    }

    @Override
    public void readFromPortableNBT (NBTTagCompound tag) {
        super.readFromPortableNBT(tag);

        materialSide = ItemStack.field_190927_a;
        if (tag.hasKey("MatS"))
            materialSide = new ItemStack(tag.getCompoundTag("MatS"));

        materialTrim = ItemStack.field_190927_a;
        if (tag.hasKey("MatT"))
            materialTrim = new ItemStack(tag.getCompoundTag("MatT"));
    }

    @Override
    public NBTTagCompound writeToPortableNBT (NBTTagCompound tag) {
        tag = super.writeToPortableNBT(tag);

        if (!materialSide.func_190926_b()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (!materialTrim.func_190926_b()) {
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
