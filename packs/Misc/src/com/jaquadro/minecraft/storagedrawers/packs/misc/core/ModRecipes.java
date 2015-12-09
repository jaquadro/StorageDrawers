package com.jaquadro.minecraft.storagedrawers.packs.misc.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.misc.StorageDrawersPack;
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

        Block[] planks = new Block[] {
            GameRegistry.findBlock("ExtrabiomesXL", "planks"),
            GameRegistry.findBlock("Highlands", "hl_woodPlanks"),
            GameRegistry.findBlock("Thaumcraft", "blockWoodenDevice" ), // 6, 7
            GameRegistry.findBlock("witchery", "witchwood"),
            GameRegistry.findBlock("ImmersiveEngineering", "treatedWood"),
            GameRegistry.findBlock("Botania", "livingwood"),
            GameRegistry.findBlock("Botania", "dreamwood")
        };

        Block[][] slabs = new Block[][] {
            new Block[] { GameRegistry.findBlock("ExtrabiomesXL", "woodslab" ), GameRegistry.findBlock("ExtrabiomesXL", "woodslab2" ) },
            new Block[] { GameRegistry.findBlock("Highlands", "hl_woodSlab"), null },
            new Block[] { GameRegistry.findBlock("Thaumcraft", "blockCosmeticSlabWood" ), null },
            new Block[] { GameRegistry.findBlock("witchery", "witchwoodslab"), null },
            new Block[] { GameRegistry.findBlock("ImmersiveEngineering", "woodenDecoration"), null }, // 2
            new Block[] { GameRegistry.findBlock("Botania", "livingwood1Slab"), null },
            new Block[] { GameRegistry.findBlock("Botania", "dreamwood1Slab"), null }
        };

        IPackDataResolver[] resolvers = StorageDrawersPack.instance.resolvers;

        for (int p = 0; p < resolvers.length; p++) {
            IPackDataResolver resolver = resolvers[p];
            for (int i = 0; i < 16; i++) {
                if (!resolver.isValidMetaValue(i))
                    continue;

                int bank = DataResolver.modBankMap[p][i];
                int meta = DataResolver.modMetaMap[p][i];
                int slabMeta = DataResolver.modSlabMetaMap[p][i];

                Block plank = planks[bank];
                if (plank != null) {
                    if (blockConfig.isBlockEnabled(nameFull1))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers1[p], blockConfig.getBlockRecipeOutput(nameFull1), i), "xxx", " y ", "xxx",
                            'x', new ItemStack(plank, 1, meta), 'y', "chestWood"));
                    if (blockConfig.isBlockEnabled(nameFull2))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers2[p], blockConfig.getBlockRecipeOutput(nameFull2), i), "xyx", "xxx", "xyx",
                            'x', new ItemStack(plank, 1, meta), 'y', "chestWood"));
                    if (blockConfig.isBlockEnabled(nameFull4))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers4[p], blockConfig.getBlockRecipeOutput(nameFull4), i), "yxy", "xxx", "yxy",
                            'x', new ItemStack(plank, 1, meta), 'y', "chestWood"));
                    if (blockConfig.isBlockEnabled(nameTrim)) {
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.trim[p], blockConfig.getBlockRecipeOutput(nameTrim), i), "xyx", "yyy", "xyx",
                            'x', "stickWood", 'y', new ItemStack(plank, 1, meta)));
                    }
                }

                Block slab = (slabMeta < 8) ? slabs[bank][0] : slabs[bank][1];
                if (slab != null) {
                    if (blockConfig.isBlockEnabled(nameHalf2))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers2[p], blockConfig.getBlockRecipeOutput(nameHalf2), i), "xyx", "xxx", "xyx",
                            'x', new ItemStack(slab, 1, slabMeta % 8), 'y', "chestWood"));
                    if (blockConfig.isBlockEnabled(nameHalf4))
                        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers4[p], blockConfig.getBlockRecipeOutput(nameHalf4), i), "yxy", "xxx", "yxy",
                            'x', new ItemStack(slab, 1, slabMeta % 8), 'y', "chestWood"));
                }
            }
        }
    }
}
