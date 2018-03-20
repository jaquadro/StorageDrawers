package net.minecraft.stats;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonSerializableSet;

public class AchievementList
{
    /** Is the smallest column used to display a achievement on the GUI. */
    public static int minDisplayColumn;
    /** Is the smallest row used to display a achievement on the GUI. */
    public static int minDisplayRow;
    /** Is the biggest column used to display a achievement on the GUI. */
    public static int maxDisplayColumn;
    /** Is the biggest row used to display a achievement on the GUI. */
    public static int maxDisplayRow;
    public static final List<Achievement> ACHIEVEMENTS = Lists.<Achievement>newArrayList();
    public static final Achievement OPEN_INVENTORY = (new Achievement("achievement.openInventory", "openInventory", 0, 0, Items.BOOK, (Achievement)null)).initIndependentStat().registerStat();
    public static final Achievement MINE_WOOD = (new Achievement("achievement.mineWood", "mineWood", 2, 1, Blocks.LOG, OPEN_INVENTORY)).registerStat();
    public static final Achievement BUILD_WORK_BENCH = (new Achievement("achievement.buildWorkBench", "buildWorkBench", 4, -1, Blocks.CRAFTING_TABLE, MINE_WOOD)).registerStat();
    public static final Achievement BUILD_PICKAXE = (new Achievement("achievement.buildPickaxe", "buildPickaxe", 4, 2, Items.WOODEN_PICKAXE, BUILD_WORK_BENCH)).registerStat();
    public static final Achievement BUILD_FURNACE = (new Achievement("achievement.buildFurnace", "buildFurnace", 3, 4, Blocks.FURNACE, BUILD_PICKAXE)).registerStat();
    public static final Achievement ACQUIRE_IRON = (new Achievement("achievement.acquireIron", "acquireIron", 1, 4, Items.IRON_INGOT, BUILD_FURNACE)).registerStat();
    /** Is the 'time to farm' achievement. */
    public static final Achievement BUILD_HOE = (new Achievement("achievement.buildHoe", "buildHoe", 2, -3, Items.WOODEN_HOE, BUILD_WORK_BENCH)).registerStat();
    public static final Achievement MAKE_BREAD = (new Achievement("achievement.makeBread", "makeBread", -1, -3, Items.BREAD, BUILD_HOE)).registerStat();
    /** Is the 'the lie' achievement. */
    public static final Achievement BAKE_CAKE = (new Achievement("achievement.bakeCake", "bakeCake", 0, -5, Items.CAKE, BUILD_HOE)).registerStat();
    public static final Achievement BUILD_BETTER_PICKAXE = (new Achievement("achievement.buildBetterPickaxe", "buildBetterPickaxe", 6, 2, Items.STONE_PICKAXE, BUILD_PICKAXE)).registerStat();
    /** Is the 'delicious fish' achievement. */
    public static final Achievement COOK_FISH = (new Achievement("achievement.cookFish", "cookFish", 2, 6, Items.COOKED_FISH, BUILD_FURNACE)).registerStat();
    public static final Achievement ON_A_RAIL = (new Achievement("achievement.onARail", "onARail", 2, 3, Blocks.RAIL, ACQUIRE_IRON)).setSpecial().registerStat();
    public static final Achievement BUILD_SWORD = (new Achievement("achievement.buildSword", "buildSword", 6, -1, Items.WOODEN_SWORD, BUILD_WORK_BENCH)).registerStat();
    public static final Achievement KILL_ENEMY = (new Achievement("achievement.killEnemy", "killEnemy", 8, -1, Items.BONE, BUILD_SWORD)).registerStat();
    /** is the 'cow tipper' achievement. */
    public static final Achievement KILL_COW = (new Achievement("achievement.killCow", "killCow", 7, -3, Items.LEATHER, BUILD_SWORD)).registerStat();
    public static final Achievement FLY_PIG = (new Achievement("achievement.flyPig", "flyPig", 9, -3, Items.SADDLE, KILL_COW)).setSpecial().registerStat();
    public static final Achievement SNIPE_SKELETON = (new Achievement("achievement.snipeSkeleton", "snipeSkeleton", 7, 0, Items.BOW, KILL_ENEMY)).setSpecial().registerStat();
    public static final Achievement DIAMONDS = (new Achievement("achievement.diamonds", "diamonds", -1, 5, Blocks.DIAMOND_ORE, ACQUIRE_IRON)).registerStat();
    public static final Achievement DIAMONDS_TO_YOU = (new Achievement("achievement.diamondsToYou", "diamondsToYou", -1, 2, Items.DIAMOND, DIAMONDS)).registerStat();
    public static final Achievement PORTAL = (new Achievement("achievement.portal", "portal", -1, 7, Blocks.OBSIDIAN, DIAMONDS)).registerStat();
    /** Is the 'Return to Sender' achievement */
    public static final Achievement GHAST = (new Achievement("achievement.ghast", "ghast", -4, 8, Items.GHAST_TEAR, PORTAL)).setSpecial().registerStat();
    public static final Achievement BLAZE_ROD = (new Achievement("achievement.blazeRod", "blazeRod", 0, 9, Items.BLAZE_ROD, PORTAL)).registerStat();
    public static final Achievement POTION = (new Achievement("achievement.potion", "potion", 2, 8, Items.POTIONITEM, BLAZE_ROD)).registerStat();
    /** Is the 'The End?' achievement */
    public static final Achievement THE_END = (new Achievement("achievement.theEnd", "theEnd", 3, 10, Items.ENDER_EYE, BLAZE_ROD)).setSpecial().registerStat();
    public static final Achievement THE_END2 = (new Achievement("achievement.theEnd2", "theEnd2", 4, 13, Blocks.DRAGON_EGG, THE_END)).setSpecial().registerStat();
    public static final Achievement ENCHANTMENTS = (new Achievement("achievement.enchantments", "enchantments", -4, 4, Blocks.ENCHANTING_TABLE, DIAMONDS)).registerStat();
    public static final Achievement OVERKILL = (new Achievement("achievement.overkill", "overkill", -4, 1, Items.DIAMOND_SWORD, ENCHANTMENTS)).setSpecial().registerStat();
    public static final Achievement BOOKCASE = (new Achievement("achievement.bookcase", "bookcase", -3, 6, Blocks.BOOKSHELF, ENCHANTMENTS)).registerStat();
    public static final Achievement BREED_COW = (new Achievement("achievement.breedCow", "breedCow", 7, -5, Items.WHEAT, KILL_COW)).registerStat();
    public static final Achievement SPAWN_WITHER = (new Achievement("achievement.spawnWither", "spawnWither", 7, 12, new ItemStack(Items.SKULL, 1, 1), THE_END2)).registerStat();
    public static final Achievement KILL_WITHER = (new Achievement("achievement.killWither", "killWither", 7, 10, Items.NETHER_STAR, SPAWN_WITHER)).registerStat();
    public static final Achievement FULL_BEACON = (new Achievement("achievement.fullBeacon", "fullBeacon", 7, 8, Blocks.BEACON, KILL_WITHER)).setSpecial().registerStat();
    public static final Achievement EXPLORE_ALL_BIOMES = (new Achievement("achievement.exploreAllBiomes", "exploreAllBiomes", 4, 8, Items.DIAMOND_BOOTS, THE_END)).setSerializableClazz(JsonSerializableSet.class).setSpecial().registerStat();
    public static final Achievement OVERPOWERED = (new Achievement("achievement.overpowered", "overpowered", 6, 4, new ItemStack(Items.GOLDEN_APPLE, 1, 1), BUILD_BETTER_PICKAXE)).setSpecial().registerStat();

    /**
     * A stub functions called to make the static initializer for this class run.
     */
    public static void init()
    {
    }
}