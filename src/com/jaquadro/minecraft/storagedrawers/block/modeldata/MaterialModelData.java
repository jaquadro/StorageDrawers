package com.jaquadro.minecraft.storagedrawers.block.modeldata;

import com.jaquadro.minecraft.chameleon.model.ModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class MaterialModelData extends ModelData
{
    @Nonnull
    private final ItemStack matFront;
    @Nonnull
    private final ItemStack matSide;
    @Nonnull
    private final ItemStack matTrim;
    @Nonnull
    private final ItemStack effectiveMatFront;
    @Nonnull
    private final ItemStack effectiveMatSide;
    @Nonnull
    private final ItemStack effectiveMatTrim;

    public MaterialModelData (TileEntityDrawers tile) {
        if (tile == null) {
            matFront = ItemStack.EMPTY;
            matSide = ItemStack.EMPTY;
            matTrim = ItemStack.EMPTY;
            effectiveMatFront = ItemStack.EMPTY;
            effectiveMatSide = ItemStack.EMPTY;
            effectiveMatTrim = ItemStack.EMPTY;
        }
        else {
            matFront = tile.getMaterialFront();
            matSide = tile.getMaterialSide();
            matTrim = tile.getMaterialTrim();
            effectiveMatFront = tile.getEffectiveMaterialFront();
            effectiveMatSide = tile.getEffectiveMaterialSide();
            effectiveMatTrim = tile.getEffectiveMaterialTrim();
        }
    }

    public MaterialModelData (TileEntityTrim tile) {
        matFront = ItemStack.EMPTY;
        effectiveMatFront = ItemStack.EMPTY;

        if (tile == null) {
            matSide = ItemStack.EMPTY;
            matTrim = ItemStack.EMPTY;
            effectiveMatSide = ItemStack.EMPTY;
            effectiveMatTrim = ItemStack.EMPTY;
        }
        else {
            matSide = tile.getMaterialSide();
            matTrim = tile.getMaterialTrim();
            effectiveMatSide = tile.getEffectiveMaterialSide();
            effectiveMatTrim = tile.getEffectiveMaterialTrim();
        }
    }

    @Nonnull
    public ItemStack getMaterialFront () {
        return matFront;
    }

    @Nonnull
    public ItemStack getMaterialSide () {
        return matSide;
    }

    @Nonnull
    public ItemStack getMaterialTrim() {
        return matTrim;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialFront () {
        return effectiveMatFront;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialSide () {
        return effectiveMatSide;
    }

    @Nonnull
    public ItemStack getEffectiveMaterialTrim() {
        return effectiveMatTrim;
    }
}
