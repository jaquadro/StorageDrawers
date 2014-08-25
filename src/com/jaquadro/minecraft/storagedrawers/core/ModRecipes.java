package com.jaquadro.minecraft.storagedrawers.core;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockWood;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ModRecipes
{
    public void init () {
        for (int i = 0; i < BlockWood.field_150096_a.length; i++) {
            GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers2, 2, i), "xyx", "xxx", "xyx",
                'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers2, 2, i), "xyx", "xxx", "xyx",
                'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
            GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers4, 4, i), "yxy", "xxx", "yxy",
                'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers4, 4, i), "yxy", "xxx", "yxy",
                'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
        }
    }
}
