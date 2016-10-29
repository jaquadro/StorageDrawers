package com.jaquadro.minecraft.storagedrawers.block.modeldata;

import com.jaquadro.minecraft.chameleon.model.ModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;
import net.minecraft.item.ItemStack;

public final class MaterialModelData extends ModelData
{
    private final ItemStack matFront;
    private final ItemStack matSide;
    private final ItemStack matTrim;
    private final ItemStack effectiveMatFront;
    private final ItemStack effectiveMatSide;
    private final ItemStack effectiveMatTrim;

    public MaterialModelData (TileEntityDrawers tile) {
        if (tile == null) {
            matFront = null;
            matSide = null;
            matTrim = null;
            effectiveMatFront = null;
            effectiveMatSide = null;
            effectiveMatTrim = null;
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
        matFront = null;
        effectiveMatFront = null;

        if (tile == null) {
            matSide = null;
            matTrim = null;
            effectiveMatSide = null;
            effectiveMatTrim = null;
        }
        else {
            matSide = tile.getMaterialSide();
            matTrim = tile.getMaterialTrim();
            effectiveMatSide = tile.getEffectiveMaterialSide();
            effectiveMatTrim = tile.getEffectiveMaterialTrim();
        }
    }

    public ItemStack getMaterialFront () {
        return matFront;
    }

    public ItemStack getMaterialSide () {
        return matSide;
    }

    public ItemStack getMaterialTrim() {
        return matTrim;
    }

    public ItemStack getEffectiveMaterialFront () {
        return effectiveMatFront;
    }

    public ItemStack getEffectiveMaterialSide () {
        return effectiveMatSide;
    }

    public ItemStack getEffectiveMaterialTrim() {
        return effectiveMatTrim;
    }
}
