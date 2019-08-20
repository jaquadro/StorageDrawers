package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModItemGroup
{
    public static final ItemGroup STORAGE_DRAWERS = (new ItemGroup("storagedrawers")
    {
        @Override
        @Nonnull
        public ItemStack createIcon () {
            return new ItemStack(ModBlocks.OAK_FULL_DRAWERS_2);
        }
    });
}
