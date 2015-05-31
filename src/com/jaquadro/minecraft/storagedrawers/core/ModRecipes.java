package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.EnumUpgradeStorage;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModRecipes
{
    public static ItemStack makeBasicDrawerItemStack (EnumBasicDrawer info, String material, int count) {
        ItemStack stack = new ItemStack(ModBlocks.basicDrawers, count, info.getMetadata());

        NBTTagCompound data = new NBTTagCompound();
        data.setString("material", material);
        stack.setTagCompound(data);

        return stack;
    }

    public void init () {
        ConfigManager config = StorageDrawers.config;

        for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
            if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                GameRegistry.addRecipe(result, "xxx", " y ", "xxx", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', Blocks.chest);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                GameRegistry.addRecipe(result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', Blocks.chest);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                GameRegistry.addRecipe(result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', Blocks.chest);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                GameRegistry.addRecipe(result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.wooden_slab, 1, material.getMetadata()), 'y', Blocks.chest);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                GameRegistry.addRecipe(result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.wooden_slab, 1, material.getMetadata()), 'y', Blocks.chest);
            }
            if (config.isBlockEnabled("trim")) {
                ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), material.getMetadata());
                GameRegistry.addRecipe(result, "xyx", "yyy", "xyx", 'x', Items.stick, 'y', new ItemStack(Blocks.planks, 1, material.getMetadata()));
            }
        }

        // Fallback recipes

        if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xxx", " y ", "xxx", 'x', "plankWood", 'y', Blocks.chest));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', "plankWood", 'y', Blocks.chest));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', "plankWood", 'y', Blocks.chest));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', "slabWood", 'y', Blocks.chest));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', "slabWood", 'y', Blocks.chest));
        }
        if (config.isBlockEnabled("trim")) {
            ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), BlockPlanks.EnumType.OAK.getMetadata());
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "yyy", "xyx", 'x', Items.stick, 'y', "slabWood"));
        }

        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compdrawers")), "xxx", "zwz", "xyx",
                'x', new ItemStack(Blocks.stone), 'y', Items.iron_ingot, 'z', new ItemStack(Blocks.piston), 'w', new ItemStack(ModBlocks.basicDrawers, 1, OreDictionary.WILDCARD_VALUE));

        if (config.isBlockEnabled("controller"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.controller), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', new ItemStack(ModBlocks.basicDrawers, 1, OreDictionary.WILDCARD_VALUE), 'w', Items.diamond);

        if (config.isBlockEnabled("controllerSlave"))
            GameRegistry.addRecipe(new ItemStack(ModBlocks.controllerSlave), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', new ItemStack(ModBlocks.basicDrawers, 1, OreDictionary.WILDCARD_VALUE), 'w', Items.gold_ingot);

        GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
            'x', Items.stick, 'y', new ItemStack(ModBlocks.basicDrawers, 1, OreDictionary.WILDCARD_VALUE));

        if (config.cache.enableStorageUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.IRON.getMetadata()), "xyx", "yzy", "xyx",
                'x', Items.iron_ingot, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.GOLD.getMetadata()), "xyx", "yzy", "xyx",
                'x', Items.gold_ingot, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.OBSIDIAN.getMetadata()), "xyx", "yzy", "xyx",
                'x', Blocks.obsidian, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.DIAMOND.getMetadata()), "xyx", "yzy", "xyx",
                'x', Items.diamond, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.EMERALD.getMetadata()), "xyx", "yzy", "xyx",
                'x', Items.emerald, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableIndicatorUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL1.getMetadata()), "wyw", "yzy", "xyx",
                'w', new ItemStack(Blocks.redstone_torch), 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL2.getMetadata()), "wyw", "yzy", "xyx",
                'w', Items.comparator, 'x', Items.redstone, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableLockUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.drawerKey), "xy ", " y ", " z ",
                'x', Items.gold_nugget, 'y', Items.gold_ingot, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableVoidUpgrades) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeVoid), "yyy", "xzx", "yyy",
                'x', Blocks.obsidian, 'y', Items.stick, 'z', ModItems.upgradeTemplate);
        }

        if (config.cache.enableShroudUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.shroudKey), ModItems.drawerKey, Items.ender_eye);
        }
    }
}
