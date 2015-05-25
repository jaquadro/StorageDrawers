package com.jaquadro.minecraft.storagedrawers.packs.bop.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.packs.bop.block.BlockDrawersPack;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModRecipes
{
    public void init () {
        ConfigManager config = StorageDrawers.config;

        Block planks = GameRegistry.findBlock("BiomesOPlenty", "planks");
        Block slab1 = GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab1");
        Block slab2 = GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab2");

        for (int i = 0; i < BlockDrawersPack.textureNames.length; i++) {
            if (BlockDrawersPack.textureNames[i] == null)
                continue;

            if (planks != null) {
                if (config.isBlockEnabled("fulldrawers1"))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers1, config.getBlockRecipeOutput("fulldrawers1"), i), "xxx", " y ", "xxx",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (config.isBlockEnabled("fulldrawers2"))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fulldrawers2"), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (config.isBlockEnabled("fulldrawers4"))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fulldrawers4"), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (config.isBlockEnabled("trim")) {
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), i), "xyx", "yyy", "xyx",
                        'x', Items.stick, 'y', new ItemStack(planks, 1, i));
                }
            }

            Block recipeSlab = (i < 8) ? slab1 : slab2;
            if (recipeSlab != null) {
                if (config.isBlockEnabled("halfdrawers2"))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfdrawers2"), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
                if (config.isBlockEnabled("halfdrawers4"))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfdrawers4"), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
            }
        }

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers1, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers4, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers4, 1, OreDictionary.WILDCARD_VALUE));
    }
}
