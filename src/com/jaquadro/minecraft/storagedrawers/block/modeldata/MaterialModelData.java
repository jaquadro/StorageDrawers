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

    public MaterialModelData (TileEntityDrawers tile) {
        matFront = tile != null ? tile.getEffectiveMaterialFront() : null;
        matSide = tile != null ? tile.getEffectiveMaterialSide() : null;
        matTrim = tile != null ? tile.getEffectiveMaterialTrim() : null;
    }

    public MaterialModelData (TileEntityTrim tile) {
        matFront = null;
        matSide = tile != null ? tile.getEffectiveMaterialSide() : null;
        matTrim = tile != null ? tile.getEffectiveMaterialTrim() : null;
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
}
