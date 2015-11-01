package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.StorageDrawersPack;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

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

        Block[][] planksSet = new Block[][] {
            new Block[] {
                GameRegistry.findBlock("Forestry", "planks"),
                GameRegistry.findBlock("Forestry", "fireproofPlanks1")
            },
            new Block [] {
                GameRegistry.findBlock("Forestry", "planks2"),
                GameRegistry.findBlock("Forestry", "fireproofPlanks2")
            }
        };

        Block[][] slabsSet = new Block[][] {
            new Block[] {
                GameRegistry.findBlock("Forestry", "slabs1"),
                GameRegistry.findBlock("Forestry", "slabs2")
            },
            new Block[]{
                GameRegistry.findBlock("Forestry", "slabs3"),
                GameRegistry.findBlock("Forestry", "slabs4")
            }
        };

        Block[] fullDrawers1 = new Block[] { ModBlocks.fullDrawers1A, ModBlocks.fullDrawers1B };
        Block[] fullDrawers2 = new Block[] { ModBlocks.fullDrawers2A, ModBlocks.fullDrawers2B };
        Block[] fullDrawers4 = new Block[] { ModBlocks.fullDrawers4A, ModBlocks.fullDrawers4B };
        Block[] halfDrawers2 = new Block[] { ModBlocks.halfDrawers2A, ModBlocks.halfDrawers2B };
        Block[] halfDrawers4 = new Block[] { ModBlocks.halfDrawers4A, ModBlocks.halfDrawers4B };
        Block[] trim = new Block[] { ModBlocks.trimA, ModBlocks.trimB };

        IPackDataResolver[] resolver = new IPackDataResolver[] { StorageDrawersPack.instance.resolver1, StorageDrawersPack.instance.resolver2 };

        for (int p = 0; p < resolver.length; p++) {
            Block[] planksGroup = planksSet[p];

            for (int i = 0; i < 16; i++) {
                if (!resolver[p].isValidMetaValue(i))
                    continue;

                for (Block planks : planksGroup) {
                    if (planks != null) {
                        if (blockConfig.isBlockEnabled(nameFull1))
                            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fullDrawers1[p], blockConfig.getBlockRecipeOutput(nameFull1), i), "xxx", " y ", "xxx",
                                'x', new ItemStack(planks, 1, i), 'y', "chestWood"));
                        if (blockConfig.isBlockEnabled(nameFull2))
                            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fullDrawers2[p], blockConfig.getBlockRecipeOutput(nameFull2), i), "xyx", "xxx", "xyx",
                                'x', new ItemStack(planks, 1, i), 'y', "chestWood"));
                        if (blockConfig.isBlockEnabled(nameFull4))
                            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fullDrawers4[p], blockConfig.getBlockRecipeOutput(nameFull4), i), "yxy", "xxx", "yxy",
                                'x', new ItemStack(planks, 1, i), 'y', "chestWood"));
                        if (blockConfig.isBlockEnabled(nameTrim)) {
                            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(trim[p], blockConfig.getBlockRecipeOutput(nameTrim), i), "xyx", "yyy", "xyx",
                                'x', "stickWood", 'y', new ItemStack(planks, 1, i)));
                        }
                    }
                }

                Block recipeSlab = (i < 8) ? slabsSet[p][0] : slabsSet[p][1];
                if (recipeSlab != null) {
                    if (blockConfig.isBlockEnabled(nameHalf2))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(halfDrawers2[p], blockConfig.getBlockRecipeOutput(nameHalf2), i), "xyx", "xxx", "xyx",
                            'x', new ItemStack(recipeSlab, 1, i), 'y', "chestWood"));
                    if (blockConfig.isBlockEnabled(nameHalf4))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(halfDrawers4[p], blockConfig.getBlockRecipeOutput(nameHalf4), i), "yxy", "xxx", "yxy",
                            'x', new ItemStack(recipeSlab, 1, i), 'y', "chestWood"));
                }
            }
        }
    }
}
