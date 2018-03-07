package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockWall;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CraftingManager
{
    /** The static instance of this class */
    private static final CraftingManager INSTANCE = new CraftingManager();
    /** A list of all the recipes added */
    private final List<IRecipe> recipes = Lists.<IRecipe>newArrayList();

    /**
     * Returns the static instance of this class
     */
    public static CraftingManager getInstance()
    {
        /** The static instance of this class */
        return INSTANCE;
    }

    private CraftingManager()
    {
        (new RecipesTools()).addRecipes(this);
        (new RecipesWeapons()).addRecipes(this);
        (new RecipesIngots()).addRecipes(this);
        (new RecipesFood()).addRecipes(this);
        (new RecipesCrafting()).addRecipes(this);
        (new RecipesArmor()).addRecipes(this);
        (new RecipesDyes()).addRecipes(this);
        this.recipes.add(new RecipesArmorDyes());
        this.recipes.add(new RecipeBookCloning());
        this.recipes.add(new RecipesMapCloning());
        this.recipes.add(new RecipesMapExtending());
        this.recipes.add(new RecipeFireworks());
        this.recipes.add(new RecipeRepairItem());
        this.recipes.add(new RecipeTippedArrow());
        (new RecipesBanners()).addRecipes(this);
        (new ShieldRecipes()).addRecipes(this);
        this.addRecipe(new ItemStack(Items.PAPER, 3), new Object[] {"###", '#', Items.REEDS});
        this.addShapelessRecipe(new ItemStack(Items.BOOK, 1), new Object[] {Items.PAPER, Items.PAPER, Items.PAPER, Items.LEATHER});
        this.addShapelessRecipe(new ItemStack(Items.WRITABLE_BOOK, 1), new Object[] {Items.BOOK, new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()), Items.FEATHER});
        this.addRecipe(new ItemStack(Blocks.OAK_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.BIRCH_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.SPRUCE_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.JUNGLE_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.ACACIA_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.DARK_OAK_FENCE, 3), new Object[] {"W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, BlockWall.EnumType.NORMAL.getMetadata()), new Object[] {"###", "###", '#', Blocks.COBBLESTONE});
        this.addRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, BlockWall.EnumType.MOSSY.getMetadata()), new Object[] {"###", "###", '#', Blocks.MOSSY_COBBLESTONE});
        this.addRecipe(new ItemStack(Blocks.NETHER_BRICK_FENCE, 6), new Object[] {"###", "###", '#', Blocks.NETHER_BRICK});
        this.addRecipe(new ItemStack(Blocks.OAK_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.BIRCH_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.ACACIA_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1), new Object[] {"#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.JUKEBOX, 1), new Object[] {"###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.DIAMOND});
        this.addRecipe(new ItemStack(Items.LEAD, 2), new Object[] {"~~ ", "~O ", "  ~", '~', Items.STRING, 'O', Items.SLIME_BALL});
        this.addRecipe(new ItemStack(Blocks.NOTEBLOCK, 1), new Object[] {"###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.REDSTONE});
        this.addRecipe(new ItemStack(Blocks.BOOKSHELF, 1), new Object[] {"###", "XXX", "###", '#', Blocks.PLANKS, 'X', Items.BOOK});
        this.addRecipe(new ItemStack(Blocks.SNOW, 1), new Object[] {"##", "##", '#', Items.SNOWBALL});
        this.addRecipe(new ItemStack(Blocks.SNOW_LAYER, 6), new Object[] {"###", '#', Blocks.SNOW});
        this.addRecipe(new ItemStack(Blocks.CLAY, 1), new Object[] {"##", "##", '#', Items.CLAY_BALL});
        this.addRecipe(new ItemStack(Blocks.BRICK_BLOCK, 1), new Object[] {"##", "##", '#', Items.BRICK});
        this.addRecipe(new ItemStack(Blocks.GLOWSTONE, 1), new Object[] {"##", "##", '#', Items.GLOWSTONE_DUST});
        this.addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1), new Object[] {"##", "##", '#', Items.QUARTZ});
        this.addRecipe(new ItemStack(Blocks.WOOL, 1), new Object[] {"##", "##", '#', Items.STRING});
        this.addRecipe(new ItemStack(Blocks.TNT, 1), new Object[] {"X#X", "#X#", "X#X", 'X', Items.GUNPOWDER, '#', Blocks.SAND});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.COBBLESTONE.getMetadata()), new Object[] {"###", '#', Blocks.COBBLESTONE});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.STONE.getMetadata()), new Object[] {"###", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.SAND.getMetadata()), new Object[] {"###", '#', Blocks.SANDSTONE});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.BRICK.getMetadata()), new Object[] {"###", '#', Blocks.BRICK_BLOCK});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata()), new Object[] {"###", '#', Blocks.STONEBRICK});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.NETHERBRICK.getMetadata()), new Object[] {"###", '#', Blocks.NETHER_BRICK});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockStoneSlab.EnumType.QUARTZ.getMetadata()), new Object[] {"###", '#', Blocks.QUARTZ_BLOCK});
        this.addRecipe(new ItemStack(Blocks.STONE_SLAB2, 6, BlockStoneSlabNew.EnumType.RED_SANDSTONE.getMetadata()), new Object[] {"###", '#', Blocks.RED_SANDSTONE});
        this.addRecipe(new ItemStack(Blocks.PURPUR_SLAB, 6, 0), new Object[] {"###", '#', Blocks.PURPUR_BLOCK});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 0), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockPlanks.EnumType.BIRCH.getMetadata()), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockPlanks.EnumType.SPRUCE.getMetadata()), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockPlanks.EnumType.JUNGLE.getMetadata()), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4), new Object[] {"###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.LADDER, 3), new Object[] {"# #", "###", "# #", '#', Items.STICK});
        this.addRecipe(new ItemStack(Items.OAK_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Items.SPRUCE_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Items.BIRCH_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Items.JUNGLE_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Items.ACACIA_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.ACACIA.getMetadata())});
        this.addRecipe(new ItemStack(Items.DARK_OAK_DOOR, 3), new Object[] {"##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.TRAPDOOR, 2), new Object[] {"###", "###", '#', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Items.IRON_DOOR, 3), new Object[] {"##", "##", "##", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Blocks.IRON_TRAPDOOR, 1), new Object[] {"##", "##", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Items.SIGN, 3), new Object[] {"###", "###", " X ", '#', Blocks.PLANKS, 'X', Items.STICK});
        this.addRecipe(new ItemStack(Items.CAKE, 1), new Object[] {"AAA", "BEB", "CCC", 'A', Items.MILK_BUCKET, 'B', Items.SUGAR, 'C', Items.WHEAT, 'E', Items.EGG});
        this.addRecipe(new ItemStack(Items.SUGAR, 1), new Object[] {"#", '#', Items.REEDS});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, BlockPlanks.EnumType.OAK.getMetadata()), new Object[] {"#", '#', new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, BlockPlanks.EnumType.SPRUCE.getMetadata()), new Object[] {"#", '#', new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, BlockPlanks.EnumType.BIRCH.getMetadata()), new Object[] {"#", '#', new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, BlockPlanks.EnumType.JUNGLE.getMetadata()), new Object[] {"#", '#', new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4), new Object[] {"#", '#', new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.PLANKS, 4, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4), new Object[] {"#", '#', new ItemStack(Blocks.LOG2, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Items.STICK, 4), new Object[] {"#", "#", '#', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Blocks.TORCH, 4), new Object[] {"X", "#", 'X', Items.COAL, '#', Items.STICK});
        this.addRecipe(new ItemStack(Blocks.TORCH, 4), new Object[] {"X", "#", 'X', new ItemStack(Items.COAL, 1, 1), '#', Items.STICK});
        this.addRecipe(new ItemStack(Items.BOWL, 4), new Object[] {"# #", " # ", '#', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Items.GLASS_BOTTLE, 3), new Object[] {"# #", " # ", '#', Blocks.GLASS});
        this.addRecipe(new ItemStack(Blocks.RAIL, 16), new Object[] {"X X", "X#X", "X X", 'X', Items.IRON_INGOT, '#', Items.STICK});
        this.addRecipe(new ItemStack(Blocks.GOLDEN_RAIL, 6), new Object[] {"X X", "X#X", "XRX", 'X', Items.GOLD_INGOT, 'R', Items.REDSTONE, '#', Items.STICK});
        this.addRecipe(new ItemStack(Blocks.ACTIVATOR_RAIL, 6), new Object[] {"XSX", "X#X", "XSX", 'X', Items.IRON_INGOT, '#', Blocks.REDSTONE_TORCH, 'S', Items.STICK});
        this.addRecipe(new ItemStack(Blocks.DETECTOR_RAIL, 6), new Object[] {"X X", "X#X", "XRX", 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, '#', Blocks.STONE_PRESSURE_PLATE});
        this.addRecipe(new ItemStack(Items.MINECART, 1), new Object[] {"# #", "###", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Items.CAULDRON, 1), new Object[] {"# #", "# #", "###", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Items.BREWING_STAND, 1), new Object[] {" B ", "###", '#', Blocks.COBBLESTONE, 'B', Items.BLAZE_ROD});
        this.addRecipe(new ItemStack(Blocks.LIT_PUMPKIN, 1), new Object[] {"A", "B", 'A', Blocks.PUMPKIN, 'B', Blocks.TORCH});
        this.addRecipe(new ItemStack(Items.CHEST_MINECART, 1), new Object[] {"A", "B", 'A', Blocks.CHEST, 'B', Items.MINECART});
        this.addRecipe(new ItemStack(Items.FURNACE_MINECART, 1), new Object[] {"A", "B", 'A', Blocks.FURNACE, 'B', Items.MINECART});
        this.addRecipe(new ItemStack(Items.TNT_MINECART, 1), new Object[] {"A", "B", 'A', Blocks.TNT, 'B', Items.MINECART});
        this.addRecipe(new ItemStack(Items.HOPPER_MINECART, 1), new Object[] {"A", "B", 'A', Blocks.HOPPER, 'B', Items.MINECART});
        this.addRecipe(new ItemStack(Items.BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Items.SPRUCE_BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Items.BIRCH_BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Items.JUNGLE_BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Items.ACACIA_BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.ACACIA.getMetadata())});
        this.addRecipe(new ItemStack(Items.DARK_OAK_BOAT, 1), new Object[] {"# #", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata())});
        this.addRecipe(new ItemStack(Items.BUCKET, 1), new Object[] {"# #", " # ", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Items.FLOWER_POT, 1), new Object[] {"# #", " # ", '#', Items.BRICK});
        this.addShapelessRecipe(new ItemStack(Items.FLINT_AND_STEEL, 1), new Object[] {new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.FLINT, 1)});
        this.addRecipe(new ItemStack(Items.BREAD, 1), new Object[] {"###", '#', Items.WHEAT});
        this.addRecipe(new ItemStack(Blocks.OAK_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.OAK.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.BIRCH_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.BIRCH.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.SPRUCE_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.SPRUCE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.JUNGLE_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockPlanks.EnumType.JUNGLE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.ACACIA_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.ACACIA.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Blocks.DARK_OAK_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4)});
        this.addRecipe(new ItemStack(Items.FISHING_ROD, 1), new Object[] {"  #", " #X", "# X", '#', Items.STICK, 'X', Items.STRING});
        this.addRecipe(new ItemStack(Items.CARROT_ON_A_STICK, 1), new Object[] {"# ", " X", '#', Items.FISHING_ROD, 'X', Items.CARROT});
        this.addRecipe(new ItemStack(Blocks.STONE_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.COBBLESTONE});
        this.addRecipe(new ItemStack(Blocks.BRICK_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.BRICK_BLOCK});
        this.addRecipe(new ItemStack(Blocks.STONE_BRICK_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.STONEBRICK});
        this.addRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.NETHER_BRICK});
        this.addRecipe(new ItemStack(Blocks.SANDSTONE_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.SANDSTONE});
        this.addRecipe(new ItemStack(Blocks.RED_SANDSTONE_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.RED_SANDSTONE});
        this.addRecipe(new ItemStack(Blocks.QUARTZ_STAIRS, 4), new Object[] {"#  ", "## ", "###", '#', Blocks.QUARTZ_BLOCK});
        this.addRecipe(new ItemStack(Items.PAINTING, 1), new Object[] {"###", "#X#", "###", '#', Items.STICK, 'X', Blocks.WOOL});
        this.addRecipe(new ItemStack(Items.ITEM_FRAME, 1), new Object[] {"###", "#X#", "###", '#', Items.STICK, 'X', Items.LEATHER});
        this.addRecipe(new ItemStack(Items.GOLDEN_APPLE), new Object[] {"###", "#X#", "###", '#', Items.GOLD_INGOT, 'X', Items.APPLE});
        this.addRecipe(new ItemStack(Items.GOLDEN_CARROT), new Object[] {"###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.CARROT});
        this.addRecipe(new ItemStack(Items.SPECKLED_MELON, 1), new Object[] {"###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.MELON});
        this.addRecipe(new ItemStack(Blocks.LEVER, 1), new Object[] {"X", "#", '#', Blocks.COBBLESTONE, 'X', Items.STICK});
        this.addRecipe(new ItemStack(Blocks.TRIPWIRE_HOOK, 2), new Object[] {"I", "S", "#", '#', Blocks.PLANKS, 'S', Items.STICK, 'I', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Blocks.REDSTONE_TORCH, 1), new Object[] {"X", "#", '#', Items.STICK, 'X', Items.REDSTONE});
        this.addRecipe(new ItemStack(Items.REPEATER, 1), new Object[] {"#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Items.REDSTONE, 'I', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Items.COMPARATOR, 1), new Object[] {" # ", "#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Items.QUARTZ, 'I', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Items.CLOCK, 1), new Object[] {" # ", "#X#", " # ", '#', Items.GOLD_INGOT, 'X', Items.REDSTONE});
        this.addRecipe(new ItemStack(Items.COMPASS, 1), new Object[] {" # ", "#X#", " # ", '#', Items.IRON_INGOT, 'X', Items.REDSTONE});
        this.addRecipe(new ItemStack(Items.MAP, 1), new Object[] {"###", "#X#", "###", '#', Items.PAPER, 'X', Items.COMPASS});
        this.addRecipe(new ItemStack(Blocks.STONE_BUTTON, 1), new Object[] {"#", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_BUTTON, 1), new Object[] {"#", '#', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Blocks.STONE_PRESSURE_PLATE, 1), new Object[] {"##", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1), new Object[] {"##", '#', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 1), new Object[] {"##", '#', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 1), new Object[] {"##", '#', Items.GOLD_INGOT});
        this.addRecipe(new ItemStack(Blocks.DISPENSER, 1), new Object[] {"###", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.BOW, 'R', Items.REDSTONE});
        this.addRecipe(new ItemStack(Blocks.DROPPER, 1), new Object[] {"###", "# #", "#R#", '#', Blocks.COBBLESTONE, 'R', Items.REDSTONE});
        this.addRecipe(new ItemStack(Blocks.PISTON, 1), new Object[] {"TTT", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, 'T', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Blocks.STICKY_PISTON, 1), new Object[] {"S", "P", 'S', Items.SLIME_BALL, 'P', Blocks.PISTON});
        this.addRecipe(new ItemStack(Items.BED, 1), new Object[] {"###", "XXX", '#', Blocks.WOOL, 'X', Blocks.PLANKS});
        this.addRecipe(new ItemStack(Blocks.ENCHANTING_TABLE, 1), new Object[] {" B ", "D#D", "###", '#', Blocks.OBSIDIAN, 'B', Items.BOOK, 'D', Items.DIAMOND});
        this.addRecipe(new ItemStack(Blocks.ANVIL, 1), new Object[] {"III", " i ", "iii", 'I', Blocks.IRON_BLOCK, 'i', Items.IRON_INGOT});
        this.addRecipe(new ItemStack(Items.LEATHER), new Object[] {"##", "##", '#', Items.RABBIT_HIDE});
        this.addShapelessRecipe(new ItemStack(Items.ENDER_EYE, 1), new Object[] {Items.ENDER_PEARL, Items.BLAZE_POWDER});
        this.addShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), new Object[] {Items.GUNPOWDER, Items.BLAZE_POWDER, Items.COAL});
        this.addShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), new Object[] {Items.GUNPOWDER, Items.BLAZE_POWDER, new ItemStack(Items.COAL, 1, 1)});
        this.addRecipe(new ItemStack(Blocks.DAYLIGHT_DETECTOR), new Object[] {"GGG", "QQQ", "WWW", 'G', Blocks.GLASS, 'Q', Items.QUARTZ, 'W', Blocks.WOODEN_SLAB});
        this.addRecipe(new ItemStack(Items.END_CRYSTAL), new Object[] {"GGG", "GEG", "GTG", 'G', Blocks.GLASS, 'E', Items.ENDER_EYE, 'T', Items.GHAST_TEAR});
        this.addRecipe(new ItemStack(Blocks.HOPPER), new Object[] {"I I", "ICI", " I ", 'I', Items.IRON_INGOT, 'C', Blocks.CHEST});
        this.addRecipe(new ItemStack(Items.ARMOR_STAND, 1), new Object[] {"///", " / ", "/_/", '/', Items.STICK, '_', new ItemStack(Blocks.STONE_SLAB, 1, BlockStoneSlab.EnumType.STONE.getMetadata())});
        this.addRecipe(new ItemStack(Blocks.END_ROD, 4), new Object[] {"/", "#", '/', Items.BLAZE_ROD, '#', Items.CHORUS_FRUIT_POPPED});
        this.addRecipe(new ItemStack(Blocks.BONE_BLOCK, 1), new Object[] {"XXX", "XXX", "XXX", 'X', new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())});
        Collections.sort(this.recipes, new Comparator<IRecipe>()
        {
            public int compare(IRecipe p_compare_1_, IRecipe p_compare_2_)
            {
                return p_compare_1_ instanceof ShapelessRecipes && p_compare_2_ instanceof ShapedRecipes ? 1 : (p_compare_2_ instanceof ShapelessRecipes && p_compare_1_ instanceof ShapedRecipes ? -1 : (p_compare_2_.getRecipeSize() < p_compare_1_.getRecipeSize() ? -1 : (p_compare_2_.getRecipeSize() > p_compare_1_.getRecipeSize() ? 1 : 0)));
            }
        });
    }

    /**
     * Adds a shaped recipe to the games recipe list.
     */
    public ShapedRecipes addRecipe(ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (String s2 : astring)
            {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[l] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, stack);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    /**
     * Adds a shapeless crafting recipe to the the game.
     */
    public void addShapelessRecipe(ItemStack stack, Object... recipeComponents)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack)object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item)object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block)object));
            }
        }

        this.recipes.add(new ShapelessRecipes(stack, list));
    }

    /**
     * Adds an IRecipe to the list of crafting recipes.
     */
    public void addRecipe(IRecipe recipe)
    {
        this.recipes.add(recipe);
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    @Nullable
    public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getCraftingResult(craftMatrix);
            }
        }

        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        ItemStack[] aitemstack = new ItemStack[craftMatrix.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            aitemstack[i] = craftMatrix.getStackInSlot(i);
        }

        return aitemstack;
    }

    /**
     * returns the List<> of all recipes
     */
    public List<IRecipe> getRecipeList()
    {
        return this.recipes;
    }
}