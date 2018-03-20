package net.minecraft.item.crafting;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes
{
    /**
     * Adds the dye recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager manager)
    {
        for (int i = 0; i < 16; ++i)
        {
            manager.addShapelessRecipe(new ItemStack(Blocks.WOOL, 1, i), new Object[] {new ItemStack(Items.DYE, 1, 15 - i), new ItemStack(Item.getItemFromBlock(Blocks.WOOL))});
            manager.addRecipe(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 8, 15 - i), new Object[] {"###", "#X#", "###", '#', new ItemStack(Blocks.HARDENED_CLAY), 'X', new ItemStack(Items.DYE, 1, i)});
            manager.addRecipe(new ItemStack(Blocks.STAINED_GLASS, 8, 15 - i), new Object[] {"###", "#X#", "###", '#', new ItemStack(Blocks.GLASS), 'X', new ItemStack(Items.DYE, 1, i)});
            manager.addRecipe(new ItemStack(Blocks.STAINED_GLASS_PANE, 16, i), new Object[] {"###", "###", '#', new ItemStack(Blocks.STAINED_GLASS, 1, i)});
        }

        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage()), new Object[] {new ItemStack(Blocks.YELLOW_FLOWER, 1, BlockFlower.EnumFlowerType.DANDELION.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.POPPY.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage()), new Object[] {Items.BONE});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage()), new Object[] {Blocks.BONE_BLOCK});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.ORANGE.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.YELLOW.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.LIME.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.GRAY.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.SILVER.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.GRAY.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 3, EnumDyeColor.SILVER.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.CYAN.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.PURPLE.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.PURPLE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.PINK.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 3, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.PINK.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 4, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[] {new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.LIGHT_BLUE.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.ALLIUM.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.SILVER.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.HOUSTONIA.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.RED_TULIP.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.ORANGE.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.ORANGE_TULIP.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.SILVER.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.WHITE_TULIP.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.PINK.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.PINK_TULIP.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.SILVER.getDyeDamage()), new Object[] {new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.OXEYE_DAISY.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.YELLOW.getDyeDamage()), new Object[] {new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.MAGENTA.getDyeDamage()), new Object[] {new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SYRINGA.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage()), new Object[] {new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.ROSE.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 2, EnumDyeColor.PINK.getDyeDamage()), new Object[] {new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta())});
        manager.addShapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()), new Object[] {new ItemStack(Items.BEETROOT, 1)});

        for (int j = 0; j < 16; ++j)
        {
            manager.addRecipe(new ItemStack(Blocks.CARPET, 3, j), new Object[] {"##", '#', new ItemStack(Blocks.WOOL, 1, j)});
        }
    }
}