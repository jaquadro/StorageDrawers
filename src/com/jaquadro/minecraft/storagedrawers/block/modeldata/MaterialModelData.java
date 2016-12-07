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

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        MaterialModelData other = (MaterialModelData)obj;
        if (!ItemStack.areItemsEqual(matFront, other.matFront))
            return false;
        if (!ItemStack.areItemsEqual(matSide, other.matSide))
            return false;
        if (!ItemStack.areItemsEqual(matTrim, other.matTrim))
            return false;

        return true;
    }

    @Override
    public int hashCode () {
        int c = 0;
        if (matFront != null) {
            c = 37 * c + (matFront.getItem() != null ? matFront.getItem().hashCode() : 0);
            c = 37 * c + matFront.getItemDamage();
        }
        else
            c = 37 * c;

        if (matSide != null) {
            c = 37 * c + (matSide.getItem() != null ? matSide.getItem().hashCode() : 0);
            c = 37 * c + matSide.getItemDamage();
        }
        else
            c = 37 * c;

        if (matTrim != null) {
            c = 37 * c + (matTrim.getItem() != null ? matTrim.getItem().hashCode() : 0);
            c = 37 * c + matTrim.getItemDamage();
        }
        else
            c = 37 * c;

        return c;
    }
}
