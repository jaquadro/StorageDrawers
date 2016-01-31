package com.jaquadro.minecraft.storagedrawers.core.api;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IExtendedDataResolver;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class PackRecipes
{
    public static void registerStandardRecipes (IExtendedDataResolver resolver) {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        String nameFull1 = blockConfig.getBlockConfigName(BlockConfiguration.BasicFull1);
        String nameFull2 = blockConfig.getBlockConfigName(BlockConfiguration.BasicFull2);
        String nameFull4 = blockConfig.getBlockConfigName(BlockConfiguration.BasicFull4);
        String nameHalf2 = blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf2);
        String nameHalf4 = blockConfig.getBlockConfigName(BlockConfiguration.BasicHalf4);
        String nameTrim = blockConfig.getBlockConfigName(BlockConfiguration.Trim);

        Block basicFull1 = resolver.getBlock(BlockConfiguration.BasicFull1);
        Block basicFull2 = resolver.getBlock(BlockConfiguration.BasicFull2);
        Block basicFull4 = resolver.getBlock(BlockConfiguration.BasicFull4);
        Block basicHalf2 = resolver.getBlock(BlockConfiguration.BasicHalf2);
        Block basicHalf4 = resolver.getBlock(BlockConfiguration.BasicHalf4);
        Block basicTrim = resolver.getBlock(BlockConfiguration.Trim);

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            Block plank = resolver.getPlankBlock(i);
            Block slab = resolver.getSlabBlock(i);
            int plankMeta = resolver.getPlankMeta(i);
            int slabMeta = resolver.getSlabMeta(i);

            if (plank != null) {
                if (blockConfig.isBlockEnabled(nameFull1) && basicFull1 != null)
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicFull1, blockConfig.getBlockRecipeOutput(nameFull1), i), "xxx", " y ", "xxx",
                        'x', new ItemStack(plank, 1, plankMeta), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameFull2) && basicFull2 != null)
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicFull2, blockConfig.getBlockRecipeOutput(nameFull2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(plank, 1, plankMeta), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameFull4) && basicFull4 != null)
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicFull4, blockConfig.getBlockRecipeOutput(nameFull4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(plank, 1, plankMeta), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameTrim) && basicTrim != null) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicTrim, blockConfig.getBlockRecipeOutput(nameTrim), i), "xyx", "yyy", "xyx",
                        'x', "stickWood", 'y', new ItemStack(plank, 1, plankMeta)));
                }
            }

            if (slab != null) {
                if (blockConfig.isBlockEnabled(nameHalf2) && basicHalf2 != null)
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicHalf2, blockConfig.getBlockRecipeOutput(nameHalf2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(slab, 1, slabMeta), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameHalf4) && basicHalf4 != null)
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(basicHalf4, blockConfig.getBlockRecipeOutput(nameHalf4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(slab, 1, slabMeta), 'y', "chestWood"));
            }
        }
    }

    public static void registerSortingRecipes (IExtendedDataResolver resolver) {
        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api == null)
            return;

        if (!Loader.isModLoaded("RefinedRelocation") || !api.userConfig().integrationConfig().isRefinedRelocationEnabled())
            return;

        IBlockConfig blockConfig = api.userConfig().blockConfig();

        String nameFull1 = blockConfig.getBlockConfigName(BlockConfiguration.SortingFull1);
        String nameFull2 = blockConfig.getBlockConfigName(BlockConfiguration.SortingFull2);
        String nameFull4 = blockConfig.getBlockConfigName(BlockConfiguration.SortingFull4);
        String nameHalf2 = blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf2);
        String nameHalf4 = blockConfig.getBlockConfigName(BlockConfiguration.SortingHalf4);
        String nameTrim = blockConfig.getBlockConfigName(BlockConfiguration.TrimSorting);

        Block basicFull1 = resolver.getBlock(BlockConfiguration.BasicFull1);
        Block basicFull2 = resolver.getBlock(BlockConfiguration.BasicFull2);
        Block basicFull4 = resolver.getBlock(BlockConfiguration.BasicFull4);
        Block basicHalf2 = resolver.getBlock(BlockConfiguration.BasicHalf2);
        Block basicHalf4 = resolver.getBlock(BlockConfiguration.BasicHalf4);
        Block basicTrim = resolver.getBlock(BlockConfiguration.Trim);

        Block sortingFull1 = resolver.getBlock(BlockConfiguration.SortingFull1);
        Block sortingFull2 = resolver.getBlock(BlockConfiguration.SortingFull2);
        Block sortingFull4 = resolver.getBlock(BlockConfiguration.SortingFull4);
        Block sortingHalf2 = resolver.getBlock(BlockConfiguration.SortingHalf2);
        Block sortingHalf4 = resolver.getBlock(BlockConfiguration.SortingHalf4);
        Block sortingTrim = resolver.getBlock(BlockConfiguration.TrimSorting);

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            if (blockConfig.isBlockEnabled(nameFull1) && sortingFull1 != null && basicFull1 != null)
                GameRegistry.addRecipe(new ItemStack(sortingFull1, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicFull1, 1, i));
            if (blockConfig.isBlockEnabled(nameFull2) && sortingFull2 != null && basicFull2 != null)
                GameRegistry.addRecipe(new ItemStack(sortingFull2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicFull2, 1, i));
            if (blockConfig.isBlockEnabled(nameFull4) && sortingFull4 != null && basicFull4 != null)
                GameRegistry.addRecipe(new ItemStack(sortingFull4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicFull4, 1, i));
            if (blockConfig.isBlockEnabled(nameHalf2) && sortingHalf2 != null && basicHalf2 != null)
                GameRegistry.addRecipe(new ItemStack(sortingHalf2, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicHalf2, 1, i));
            if (blockConfig.isBlockEnabled(nameHalf4) && sortingHalf4 != null && basicHalf4 != null)
                GameRegistry.addRecipe(new ItemStack(sortingHalf4, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicHalf4, 1, i));
            if (blockConfig.isBlockEnabled(nameTrim) && sortingTrim != null && basicTrim != null)
                GameRegistry.addRecipe(new ItemStack(sortingTrim, 1, i), "x x", " y ", "x x",
                    'x', Items.gold_nugget, 'y', new ItemStack(basicTrim, 1, i));
        }
    }
}
