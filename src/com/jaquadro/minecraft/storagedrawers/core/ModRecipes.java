package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockWood;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModRecipes
{
    public void init () {
        ConfigManager config = StorageDrawers.config;

        for (int i = 0; i < BlockWood.field_150096_a.length; i++) {
            if (config.isBlockEnabled("fullDrawers2"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fullDrawers2"), i), "xyx", "xxx", "xyx",
                    'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("halfDrawers2"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfDrawers2"), i), "xyx", "xxx", "xyx",
                    'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("fullDrawers4"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fullDrawers4"), i), "yxy", "xxx", "yxy",
                    'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("halfDrawers4"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfDrawers4"), i), "yxy", "xxx", "yxy",
                    'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
        }

        if (config.isBlockEnabled("compDrawers"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compDrawers")), "xxx", "zwz", "xyx",
                'x', new ItemStack(Blocks.stone), 'y', Items.iron_ingot, 'z', new ItemStack(Blocks.piston), 'w', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));

        if (config.isBlockEnabled("fullDrawers2"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("halfDrawers2"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("fullDrawers4"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers4, 1, OreDictionary.WILDCARD_VALUE));
        if (config.isBlockEnabled("halfDrawers4"))
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers4, 1, OreDictionary.WILDCARD_VALUE));

        if (config.cache.enableStorageUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgrade, 1, 2), "xyx", "yzy", "xyx",
                'x', Items.iron_ingot, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgrade, 1, 3), "xyx", "yzy", "xyx",
                'x', Items.gold_ingot, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgrade, 1, 4), "xyx", "yzy", "xyx",
                'x', Blocks.obsidian, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgrade, 1, 5), "xyx", "yzy", "xyx",
                'x', Items.diamond, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgrade, 1, 6), "xyx", "yzy", "xyx",
                'x', Items.emerald, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableIndicatorUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, 1), "xyx", "yzy", "xyx",
                'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, 2), "wyw", "yzy", "xyx",
                'w', new ItemStack(Blocks.redstone_torch), 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, 3), "wyw", "yzy", "xyx",
                'w', Items.comparator, 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }
    }
}
