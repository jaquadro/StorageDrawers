package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.mc1710.recipes.ShapedRecipeOre;
import net.minecraft.block.BlockWood;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModRecipes
{
    public void init () {
        ConfigManager config = StorageDrawers.config;

        for (int i = 0; i < BlockWood.field_150096_a.length; i++) {
            if (config.isBlockEnabled("fulldrawers1"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers1, config.getBlockRecipeOutput("fulldrawers1"), i), "xxx", " y ", "xxx",
                    'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("fulldrawers2"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fulldrawers2"), i), "xyx", "xxx", "xyx",
                    'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("halfdrawers2"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfdrawers2"), i), "xyx", "xxx", "xyx",
                    'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("fulldrawers4"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fulldrawers4"), i), "yxy", "xxx", "yxy",
                    'x', new ItemStack(Blocks.planks, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("halfdrawers4"))
                GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfdrawers4"), i), "yxy", "xxx", "yxy",
                    'x', new ItemStack(Blocks.wooden_slab, 1, i), 'y', Blocks.chest);
            if (config.isBlockEnabled("trim")) {
                GameRegistry.addRecipe(new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), i), "xyx", "yyy", "xyx",
                    'x', Items.stick, 'y', new ItemStack(Blocks.planks, 1, i));
            }
        }

        // Fallback recipes
        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers1, config.getBlockRecipeOutput("fulldrawers1"), 0), "xxx", " y ", "xxx",
                'x', "plankWood", 'y', Blocks.chest));
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fulldrawers2"), 0), "xyx", "xxx", "xyx",
                'x', "plankWood", 'y', Blocks.chest));
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfdrawers2"), 0), "xyx", "xxx", "xyx",
                'x', "plankWood", 'y', Blocks.chest));
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fulldrawers4"), 0), "yxy", "xxx", "yxy",
                'x', "slabWood", 'y', Blocks.chest));
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfdrawers4"), 0), "yxy", "xxx", "yxy",
                'x', "slabWood", 'y', Blocks.chest));
        if (config.isBlockEnabled("trim"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), 0), "xyx", "yyy", "xyx",
                'x', Items.stick, 'y', "plankWood"));

        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compdrawers")), "xxx", "zwz", "xyx",
                'x', new ItemStack(Blocks.stone), 'y', Items.iron_ingot, 'z', new ItemStack(Blocks.piston), 'w', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));

        if (config.isBlockEnabled("controller"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.controller), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE), 'w', Items.diamond);

        if (config.isBlockEnabled("controllerSlave"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.controllerSlave), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE), 'w', Items.gold_ingot);

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
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, 1), "wyw", "yzy", "xyx",
                'w', new ItemStack(Blocks.redstone_torch), 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, 2), "wyw", "yzy", "xyx",
                'w', Items.comparator, 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableLockUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeLock), "xy ", " y ", " z ",
                'x', Items.gold_nugget, 'y', Items.gold_ingot, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableVoidUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeVoid), "yyy", "xzx", "yyy",
                'x', Blocks.obsidian, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableShroudUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.shroudKey), ModItems.upgradeLock, Items.ender_eye);
        }
    }
}
