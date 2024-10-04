package com.jaquadro.minecraft.storagedrawers.api.framing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public interface IFramedMaterials extends INBTSerializable<CompoundTag>
{
    @NotNull
    ItemStack getHostBlock ();

    void setHostBlock (@NotNull ItemStack stack);

    @NotNull
    ItemStack getMaterial (FrameMaterial material);

    void setMaterial (FrameMaterial material, @NotNull ItemStack stack);
}
