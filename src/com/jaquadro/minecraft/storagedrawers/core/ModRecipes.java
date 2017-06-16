package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class ModRecipes
{
    private static final ResourceLocation EMPTY_GROUP = new ResourceLocation("", "");

    @Nonnull
    public static ItemStack makeBasicDrawerItemStack (EnumBasicDrawer info, String material, int count) {
        ItemStack stack = new ItemStack(ModBlocks.basicDrawers, count, info.getMetadata());

        NBTTagCompound data = new NBTTagCompound();
        data.setString("material", material);
        stack.setTagCompound(data);

        return stack;
    }

    @Nonnull
    public static ItemStack makeCustomDrawerItemStack (EnumBasicDrawer info, int count) {
        return new ItemStack(ModBlocks.customDrawers, count, info.getMetadata());
    }

    public void init () {
        ConfigManager config = StorageDrawers.config;

        //RecipeSorter.register("storagedrawers:FallbackShapedOreRecipe", FallbackShapedOreRecipe.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore");

        for (BlockPlanks.EnumType material : BlockPlanks.EnumType.values()) {
            ItemStack pl = new ItemStack(Blocks.PLANKS, 1, material.getMetadata());
            ItemStack sl = new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata());

            if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xxx", " y ", "xxx", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                    .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL1.getUnlocalizedName() + "_" + material.toString()));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                    .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL2.getUnlocalizedName() + "_" + material.toString()));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()), 'y', "chestWood")
                    .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.FULL4.getUnlocalizedName() + "_" + material.toString()));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "xxx", "xyx", 'x', new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata()), 'y', "chestWood")
                    .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF2.getUnlocalizedName() + "_" + material.toString()));
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, material.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "yxy", "xxx", "yxy", 'x', new ItemStack(Blocks.WOODEN_SLAB, 1, material.getMetadata()), 'y', "chestWood")
                    .setRegistryName(result.getItem().getRegistryName() + "_" + EnumBasicDrawer.HALF4.getUnlocalizedName() + "_" + material.toString()));
            }
            if (config.isBlockEnabled("trim")) {
                ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), material.getMetadata());
                GameRegistry.register(new ShapedOreRecipe(EMPTY_GROUP, result, "xyx", "yyy", "xyx", 'x', "stickWood", 'y', new ItemStack(Blocks.PLANKS, 1, material.getMetadata()))
                    .setRegistryName(result.getItem().getRegistryName() + "_" + material.toString()));
            }
        }

        // Fallback recipes

        /*if (config.cache.enableFallbackRecipes) {
            String pl = "plankWood";
            String sl = "slabWood";

            if (config.isBlockEnabled(EnumBasicDrawer.FULL1.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL1, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, pl, pl, pl, null, ch, null, pl, pl, pl);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, pl, ch, pl, pl, pl, pl, pl, ch, pl);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.FULL4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.FULL4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, ch, pl, ch, pl, pl, pl, ch, pl, ch);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF2.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF2, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, sl, ch, sl, sl, sl, sl, sl, ch, sl);
            }
            if (config.isBlockEnabled(EnumBasicDrawer.HALF4.getUnlocalizedName())) {
                ItemStack result = makeBasicDrawerItemStack(EnumBasicDrawer.HALF4, BlockPlanks.EnumType.OAK.getName(), config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, ch, sl, ch, sl, sl, sl, ch, sl, ch);
            }
            if (config.isBlockEnabled("trim")) {
                ItemStack result = new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), BlockPlanks.EnumType.OAK.getMetadata());
                RecipeHelper.addShapedRecipe(result, 3, 3, st, pl, st, pl, pl, pl, st, pl, st);
            }
        }*/

        /*Block obs = Blocks.OBSIDIAN;
        Block stone = Blocks.STONE;
        Block pist = Blocks.PISTON;
        Item comp = Items.COMPARATOR;
        Item upg = ModItems.upgradeTemplate;
        Item flint = Items.FLINT;
        String dw = "drawerBasic";
        String ii = "ingotIron";
        String ig = "ingotGold";
        String gd = "gemDiamond";
        String ge = "gemEmerald";

        if (config.isBlockEnabled("compdrawers"))
            RecipeHelper.addShapedRecipe(new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compdrawers")), 3, 3,
                stone, stone, stone, pist, dw, pist, stone, ii, stone);

        if (config.isBlockEnabled("controller"))
            RecipeHelper.addShapedRecipe(new ItemStack(ModBlocks.controller), 3, 3,
                stone, stone, stone, comp, dw, comp, stone, gd, stone);

        if (config.isBlockEnabled("controllerSlave"))
            RecipeHelper.addShapedRecipe(new ItemStack(ModBlocks.controllerSlave), 3, 3,
                stone, stone, stone, comp, dw, comp, stone, ig, stone);

        if (config.cache.enableStorageUpgrades) {
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.IRON.getMetadata()), 3, 3,
                st, st, st, ii, upg, ii, st, st, st);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.GOLD.getMetadata()), 3, 3,
                st, st, st, ig, upg, ig, st, st, st);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.OBSIDIAN.getMetadata()), 3, 3,
                st, st, st, obs, upg, obs, st, st, st);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.DIAMOND.getMetadata()), 3, 3,
                st, st, st, gd, upg, gd, st, st, st);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStorage, 1, EnumUpgradeStorage.EMERALD.getMetadata()), 3, 3,
                st, st, st, ge, upg, ge, st, st, st);

            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeOneStack, 1), 3, 3,
                st, st, st, flint, upg, flint, st, st, st);
        }

        Block rstorch = Blocks.REDSTONE_TORCH;
        String dr = "dustRedstone";
        String ng = "nuggetGold";

        if (config.cache.enableIndicatorUpgrades) {
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL1.getMetadata()), 3, 3,
                rstorch, st, rstorch, st, upg, st, dr, st, dr);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeStatus, 1, EnumUpgradeStatus.LEVEL2.getMetadata()), 3, 3,
                comp, st, comp, st, upg, st, dr, st, dr);
        }

        if (config.cache.enableLockUpgrades) {
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.drawerKey), 2, 3,
                ng, ig, null, ig, null, upg);
            RecipeHelper.addShapelessRecipe(new ItemStack(ModBlocks.keyButton, 1, EnumKeyType.DRAWER.getMetadata()),
                Blocks.STONE_BUTTON, ModItems.drawerKey);
        }

        if (config.cache.enableVoidUpgrades) {
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeVoid), 3, 3,
                obs, obs, obs, obs, upg, obs, obs, obs, obs);
        }

        if (config.cache.enableRedstoneUpgrades) {
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeRedstone, 1, EnumUpgradeRedstone.COMBINED.getMetadata()), 3, 3,
                dr, st, dr, st, upg, st, dr, st, dr);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeRedstone, 1, EnumUpgradeRedstone.MAX.getMetadata()), 3, 3,
                dr, dr, dr, st, upg, st, st, st, st);
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeRedstone, 1, EnumUpgradeRedstone.MIN.getMetadata()), 3, 3,
                st, st, st, st, upg, st, dr, dr, dr);
        }

        if (config.cache.enableShroudUpgrades) {
            RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.shroudKey),
                Items.ENDER_EYE, ModItems.drawerKey);
            RecipeHelper.addShapelessRecipe(new ItemStack(ModBlocks.keyButton, 1, EnumKeyType.CONCEALMENT.getMetadata()),
                Blocks.STONE_BUTTON, ModItems.shroudKey);
        }

        if (config.cache.enableQuantifiableUpgrades) {
            RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.quantifyKey),
                Items.WRITABLE_BOOK, ModItems.drawerKey);
            RecipeHelper.addShapelessRecipe(new ItemStack(ModBlocks.keyButton, 1, EnumKeyType.QUANTIFY.getMetadata()),
                Blocks.STONE_BUTTON, ModItems.quantifyKey);
        }

        if (config.cache.enablePersonalUpgrades) {
            RecipeHelper.addShapelessRecipe(new ItemStack(ModItems.personalKey),
                Items.NAME_TAG, ModItems.drawerKey);
            RecipeHelper.addShapelessRecipe(new ItemStack(ModBlocks.keyButton, 1, EnumKeyType.PERSONAL.getMetadata()),
                Blocks.STONE_BUTTON, ModItems.personalKey);
        }

        if (config.cache.enableTape) {
            Item paper = Items.PAPER;
            RecipeHelper.addShapedRecipe(new ItemStack(ModItems.tape), 3, 2,
                null, "slimeball", null, paper, paper, paper);
        }

        Block trim = ModBlocks.trim;

        if (config.cache.enableFramedDrawers) {
            String pl = "plankWood";
            String sl = "slabWood";

            RecipeHelper.addShapedRecipe(new ItemStack(ModBlocks.framingTable), 3, 2,
                trim, trim, trim, trim, null, trim);

            if (config.isBlockEnabled("fulldrawers1")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL1, config.getBlockRecipeOutput(EnumBasicDrawer.FULL1.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, st, st, st, null, ch, null, st, st, st);
            }
            if (config.isBlockEnabled("fulldrawers2")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL2, config.getBlockRecipeOutput(EnumBasicDrawer.FULL2.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, st, ch, st, st, pl, st, st, ch, st);
            }
            if (config.isBlockEnabled("halfdrawers2")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.HALF2, config.getBlockRecipeOutput(EnumBasicDrawer.HALF2.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, ch, st, ch, st, sl, st, ch, st, ch);
            }
            if (config.isBlockEnabled("fulldrawers4")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.FULL4, config.getBlockRecipeOutput(EnumBasicDrawer.FULL4.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, ch, st, ch, st, pl, st, ch, st, ch);
            }
            if (config.isBlockEnabled("halfdrawers4")) {
                ItemStack result = makeCustomDrawerItemStack(EnumBasicDrawer.HALF4, config.getBlockRecipeOutput(EnumBasicDrawer.HALF4.getUnlocalizedName()));
                RecipeHelper.addShapedRecipe(result, 3, 3, ch, st, ch, st, sl, st, ch, st, ch);
            }
            if (config.isBlockEnabled("trim")) {
                ItemStack result = new ItemStack(ModBlocks.customTrim, config.getBlockRecipeOutput("trim"));
                RecipeHelper.addShapedRecipe(result, 3, 3, pl, st, pl, st, pl, st, pl, st, pl);
            }
        }

        RecipeHelper.addShapedRecipe(new ItemStack(ModItems.upgradeTemplate, 2), 3, 3,
            st, st, st, st, dw, st, st, st, st);*/

        //RecipeSorter.register("storagedrawers:UpgradeTemplate", TemplateRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        //CraftingManager.getInstance().getRecipeList().add(new TemplateRecipe());
    }
}
