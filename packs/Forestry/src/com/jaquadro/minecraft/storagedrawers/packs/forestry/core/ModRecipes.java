package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.block.BlockDrawersPack;
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

        String[][] textureNames = new String[][] {
            BlockDrawersPack.textureNames1,
            BlockDrawersPack.textureNames2
        };

        Block[] planksSet = new Block[] {
            GameRegistry.findBlock("Forestry", "planks"),
            GameRegistry.findBlock("Forestry", "planks2")
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

        for (int p = 0; p < planksSet.length; p++) {
            Block planks = planksSet[p];

            for (int i = 0; i < textureNames[p].length; i++) {
                if (textureNames[p][i] == null)
                    continue;

                if (planks != null) {
                    if (config.isBlockEnabled("fulldrawers1"))
                        GameRegistry.addRecipe(new ItemStack(fullDrawers1[p], config.getBlockRecipeOutput("fulldrawers1"), i), "xxx", " y ", "xxx",
                            'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                    if (config.isBlockEnabled("fulldrawers2"))
                        GameRegistry.addRecipe(new ItemStack(fullDrawers2[p], config.getBlockRecipeOutput("fulldrawers2"), i), "xyx", "xxx", "xyx",
                            'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                    if (config.isBlockEnabled("fulldrawers4"))
                        GameRegistry.addRecipe(new ItemStack(fullDrawers4[p], config.getBlockRecipeOutput("fulldrawers4"), i), "yxy", "xxx", "yxy",
                            'x', new ItemStack(planks, 1, i), 'y', Blocks.chest);
                    if (config.isBlockEnabled("trim")) {
                        GameRegistry.addRecipe(new ItemStack(trim[p], config.getBlockRecipeOutput("trim"), i), "xyx", "yyy", "xyx",
                            'x', Items.stick, 'y', new ItemStack(planks, 1, i));
                    }
                }

                Block recipeSlab = (i < 8) ? slabsSet[p][0] : slabsSet[p][1];
                if (recipeSlab != null) {
                    if (config.isBlockEnabled("halfdrawers2"))
                        GameRegistry.addRecipe(new ItemStack(halfDrawers2[p], config.getBlockRecipeOutput("halfdrawers2"), i), "xyx", "xxx", "xyx",
                            'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
                    if (config.isBlockEnabled("halfdrawers4"))
                        GameRegistry.addRecipe(new ItemStack(halfDrawers4[p], config.getBlockRecipeOutput("halfdrawers4"), i), "yxy", "xxx", "yxy",
                            'x', new ItemStack(recipeSlab, 1, i), 'y', Blocks.chest);
                }
            }
        }

        if (config.isBlockEnabled("fulldrawers1")) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers1A, 1, OreDictionary.WILDCARD_VALUE));
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers1B, 1, OreDictionary.WILDCARD_VALUE));
        }
        if (config.isBlockEnabled("fulldrawers2")) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers2A, 1, OreDictionary.WILDCARD_VALUE));
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers2B, 1, OreDictionary.WILDCARD_VALUE));
        }
        if (config.isBlockEnabled("halfdrawers2")) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers2A, 1, OreDictionary.WILDCARD_VALUE));
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers2B, 1, OreDictionary.WILDCARD_VALUE));
        }
        if (config.isBlockEnabled("fulldrawers4")) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers4A, 1, OreDictionary.WILDCARD_VALUE));
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.fullDrawers4B, 1, OreDictionary.WILDCARD_VALUE));
        }
        if (config.isBlockEnabled("halfdrawers4")) {
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers4A, 1, OreDictionary.WILDCARD_VALUE));
            GameRegistry.addRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
                'x', Items.stick, 'y', new ItemStack(ModBlocks.halfDrawers4B, 1, OreDictionary.WILDCARD_VALUE));
        }
    }
}
