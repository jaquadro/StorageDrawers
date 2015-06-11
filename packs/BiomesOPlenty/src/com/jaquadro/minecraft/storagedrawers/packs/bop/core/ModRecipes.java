package com.jaquadro.minecraft.storagedrawers.packs.bop.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.bop.StorageDrawersPack;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModRecipes
{
    public void init () {
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

        Block planks = GameRegistry.findBlock("BiomesOPlenty", "planks");
        Block slab1 = GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab1");
        Block slab2 = GameRegistry.findBlock("BiomesOPlenty", "woodenSingleSlab2");

        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            if (planks != null) {
                if (blockConfig.isBlockEnabled(nameFull1))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers1, blockConfig.getBlockRecipeOutput(nameFull1), i), "xxx", " y ", "xxx",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (blockConfig.isBlockEnabled(nameFull2))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers2, blockConfig.getBlockRecipeOutput(nameFull2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (blockConfig.isBlockEnabled(nameFull4))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.fullDrawers4, blockConfig.getBlockRecipeOutput(nameFull4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                if (blockConfig.isBlockEnabled(nameTrim)) {
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.trim, blockConfig.getBlockRecipeOutput(nameTrim), i), "xyx", "yyy", "xyx",
                        'x', Items.stick, 'y', new ItemStack(planks, 1, i));
                }
            }

            Block recipeSlab = (i < 8) ? slab1 : slab2;
            if (recipeSlab != null) {
                if (blockConfig.isBlockEnabled(nameHalf2))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers2, blockConfig.getBlockRecipeOutput(nameHalf2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
                if (blockConfig.isBlockEnabled(nameHalf4))
                    GameRegistry.addRecipe(new ItemStack(ModBlocks.halfDrawers4, blockConfig.getBlockRecipeOutput(nameHalf4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
            }
        }

        ItemStack itemTemplate = new ItemStack(GameRegistry.findItem("StorageDrawers", "upgradeTemplate"), 2);

        if (blockConfig.isBlockEnabled(nameFull1))
            GameRegistry.addRecipe(itemTemplate, "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers1, 1, OreDictionary.WILDCARD_VALUE));
        if (blockConfig.isBlockEnabled(nameFull2))
            GameRegistry.addRecipe(itemTemplate, "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (blockConfig.isBlockEnabled(nameFull4))
            GameRegistry.addRecipe(itemTemplate, "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers2, 1, OreDictionary.WILDCARD_VALUE));
        if (blockConfig.isBlockEnabled(nameHalf2))
            GameRegistry.addRecipe(itemTemplate, "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers4, 1, OreDictionary.WILDCARD_VALUE));
        if (blockConfig.isBlockEnabled(nameHalf4))
            GameRegistry.addRecipe(itemTemplate, "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers4, 1, OreDictionary.WILDCARD_VALUE));
    }
}
