/*package com.jaquadro.minecraft.storagedrawers.block.tile.tiledata;

import com.jaquadro.minecraft.chameleon.block.tiledata.TileDataShim;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class MaterialData extends TileDataShim
{
    @Nonnull
    private ItemStack materialSide;
    @Nonnull
    private ItemStack materialFront;
    @Nonnull
    private ItemStack materialTrim;

    public MaterialData () {
        materialSide = ItemStack.EMPTY;
        materialFront = ItemStack.EMPTY;
        materialTrim = ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getFront () {
        return materialFront;
    }

    @Nonnull
    public ItemStack getTrim () {
        return materialTrim;
    }

    @Nonnull
    public ItemStack getEffectiveSide () {
        return materialSide;
    }

    @Nonnull
    public ItemStack getEffectiveFront () {
        return !materialFront.isEmpty() ? materialFront : materialSide;
    }

    @Nonnull
    public ItemStack getEffectiveTrim () {
        return !materialTrim.isEmpty() ? materialTrim : materialSide;
    }

    public void setSide (@Nonnull ItemStack material) {
        materialSide = material;
    }

    public void setFront (@Nonnull ItemStack material) {
        materialFront = material;
    }

    public void setTrim (@Nonnull ItemStack material) {
        materialTrim = material;
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        materialSide = ItemStack.EMPTY;
        if (tag.hasKey("MatS"))
            materialSide = new ItemStack(tag.getCompoundTag("MatS"));

        materialFront = ItemStack.EMPTY;
        if (tag.hasKey("MatF"))
            materialFront = new ItemStack(tag.getCompoundTag("MatF"));

        materialTrim = ItemStack.EMPTY;
        if (tag.hasKey("MatT"))
            materialTrim = new ItemStack(tag.getCompoundTag("MatT"));
    }

    @Override
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        if (!materialSide.isEmpty()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialSide.writeToNBT(itag);
            tag.setTag("MatS", itag);
        }

        if (!materialFront.isEmpty()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialFront.writeToNBT(itag);
            tag.setTag("MatF", itag);
        }

        if (!materialTrim.isEmpty()) {
            NBTTagCompound itag = new NBTTagCompound();
            materialTrim.writeToNBT(itag);
            tag.setTag("MatT", itag);
        }

        return tag;
    }
}
*/