package com.jaquadro.minecraft.storagedrawers.packs.erebus.core;

import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.StorageDrawersPack;
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

        Block planks = GameRegistry.findBlock("erebus", "planks");

        IPackDataResolver resolver = StorageDrawersPack.instance.resolver;

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i))
                continue;

            int src = i;
            if (resolver.getUnlocalizedName(i).equals("scorched")) {
                planks = GameRegistry.findBlock("erebus", "planks_scorched");
                src = 0;
            }
            else if (resolver.getUnlocalizedName(i).equals("varnished")) {
                planks = GameRegistry.findBlock("erebus", "planks_varnished");
                src = 0;
            }

            if (planks != null) {
                if (blockConfig.isBlockEnabled(nameFull1))
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers1, blockConfig.getBlockRecipeOutput(nameFull1), i), "xxx", " y ", "xxx",
                        'x', new ItemStack(planks, 1, src), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameFull2))
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers2, blockConfig.getBlockRecipeOutput(nameFull2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(planks, 1, src), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameFull4))
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fullDrawers4, blockConfig.getBlockRecipeOutput(nameFull4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(planks, 1, src), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameTrim)) {
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.trim, blockConfig.getBlockRecipeOutput(nameTrim), i), "xyx", "yyy", "xyx",
                        'x', "stickWood", 'y', new ItemStack(planks, 1, src)));
                }
            }

            Block slab = GameRegistry.findBlock("erebus", getPlankName(resolver.getUnlocalizedName(i)));

            if (slab != null) {
                if (blockConfig.isBlockEnabled(nameHalf2))
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers2, blockConfig.getBlockRecipeOutput(nameHalf2), i), "xyx", "xxx", "xyx",
                        'x', new ItemStack(slab, 1), 'y', "chestWood"));
                if (blockConfig.isBlockEnabled(nameHalf4))
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.halfDrawers4, blockConfig.getBlockRecipeOutput(nameHalf4), i), "yxy", "xxx", "yxy",
                        'x', new ItemStack(slab, 1), 'y', "chestWood"));
            }
        }
    }

    private String getPlankName (String name) {
        return "slabPlanks" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
