package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
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

    public static ItemStack makeCustomDrawerItemStack (EnumBasicDrawer info, int count) {
        return new ItemStack(ModBlocks.customDrawers, count, info.getMetadata());
    }

    public void init () {
        ConfigManager config = StorageDrawers.config;

        for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
            if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xxx", " y ", "xxx", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', "chestWood"));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', "chestWood"));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.planks, 1, material.getMetadata()), 'y', "chestWood"));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.wooden_slab, 1, material.getMetadata()), 'y', "chestWood"));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.wooden_slab, 1, material.getMetadata()), 'y', "chestWood"));
            }
            if (config.isBlockEnabled("trim")) {
                ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), material.getMetadata());
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "yyy", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.planks, 1, material.getMetadata())));
            }
        }

        // Fallback recipes

        if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xxx", " y ", "xxx", 'x', "plankWood", 'y', "chestWood"));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', "plankWood", 'y', "chestWood"));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', "plankWood", 'y', "chestWood"));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xxx", "xyx", 'x', "slabWood", 'y', "chestWood"));
        }
        if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
            ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xxx", "yxy", 'x', "slabWood", 'y', "chestWood"));
        }
        if (config.isBlockEnabled("trim")) {
            ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), BlockPlanks.EnumType.OAK.getMetadata());
            GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "yyy", "xyx", 'x', "stickWood", 'y', "slabWood"));
        }

        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compdrawers")), "xxx", "zwz", "xyx",
                'x', new ItemStack(Blocks.stone), 'y', "ingotIron", 'z', new ItemStack(Blocks.piston), 'w', "drawerBasic"));

        if (config.isBlockEnabled("controller"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.controller), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', "drawerBasic", 'w', "gemDiamond"));

        if (config.isBlockEnabled("controllerSlave"))
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.controllerSlave), "xxx", "yzy", "xwx",
                'x', new ItemStack(Blocks.stone), 'y', Items.comparator, 'z', "drawerBasic", 'w', "ingotGold"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
            'x', "stickWood", 'y', new ItemStack(ModBlocks.basicDrawers, 1, OreDictionary.WILDCARD_VALUE)));

        if (config.cache.enableStorageUpgrades) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.IRON.getMetadata()), "xyx", "yzy", "xyx",
                'x', "ingotIron", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.GOLD.getMetadata()), "xyx", "yzy", "xyx",
                'x', "ingotGold", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.OBSIDIAN.getMetadata()), "xyx", "yzy", "xyx",
                'x', Blocks.obsidian, 'y', "stickWood", 'z', ModItems.upgradeTemplate));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.DIAMOND.getMetadata()), "xyx", "yzy", "xyx",
                'x', "gemDiamond", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.EMERALD.getMetadata()), "xyx", "yzy", "xyx",
                'x', "gemEmerald", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
        }

        if (config.cache.enableIndicatorUpgrades) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL1.getMetadata()), "wyw", "yzy", "xyx",
                'w', new ItemStack(Blocks.redstone_torch), 'x', "dustRedstone", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL2.getMetadata()), "wyw", "yzy", "xyx",
                'w', Items.comparator, 'x', "dustRedstone", 'y', "stickWood", 'z', ModItems.upgradeTemplate));
        }

        if (config.cache.enableLockUpgrades) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.drawerKey), "xy ", " y ", " z ",
                'x', "nuggetGold", 'y', "ingotGold", 'z', ModItems.upgradeTemplate));
        }

        if (config.cache.enableVoidUpgrades) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeVoid), "yyy", "xzx", "yyy",
                'x', Blocks.obsidian, 'y', "stickWood", 'z', ModItems.upgradeTemplate));
        }

        if (config.cache.enableShroudUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.shroudKey), ModItems.drawerKey, Items.ender_eye);
        }

        if (config.cache.enablePersonalUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.personalKey), ModItems.drawerKey, Items.name_tag);
        }

        if (config.cache.enableTape) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.tape), " x ", "yyy",
                'x', "slimeball", 'y', Items.paper));
        }

        if (config.cache.enableFramedDrawers) {
            GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.framingTable), "xxx", "x x", 'x', ModBlocks.trim);

            if (config.isBlockEnabled("fulldrawers1")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL1, config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xxx", " y ", "xxx", 'x', "stickWood", 'y', "chestWood"));
            }
            if (config.isBlockEnabled("fulldrawers2")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL2, config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xzx", "xyx", 'x', "stickWood", 'y', "chestWood", 'z', "plankWood"));
            }
            if (config.isBlockEnabled("halfdrawers2")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.HALF2, config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "xyx", "xzx", "xyx", 'x', "stickWood", 'y', "chestWood", 'z', "slabWood"));
            }
            if (config.isBlockEnabled("fulldrawers4")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL4, config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xzx", "yxy", 'x', "stickWood", 'y', "chestWood", 'z', "plankWood"));
            }
            if (config.isBlockEnabled("halfdrawers4")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.HALF4, config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                GameRegistry.addRecipe(new ShapedOreRecipe(result, "yxy", "xzx", "yxy", 'x', "stickWood", 'y', "chestWood", 'z', "slabWood"));
            }
        }
    }
}
