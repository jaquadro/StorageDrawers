package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome extends net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl<Biome>
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    protected static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    protected static final IBlockState RED_SANDSTONE = Blocks.RED_SANDSTONE.getDefaultState();
    protected static final IBlockState SANDSTONE = Blocks.SANDSTONE.getDefaultState();
    protected static final IBlockState ICE = Blocks.ICE.getDefaultState();
    protected static final IBlockState WATER = Blocks.WATER.getDefaultState();
    public static final Set<Biome> EXPLORATION_BIOMES_LIST = Sets.<Biome>newHashSet();
    public static final ObjectIntIdentityMap<Biome> MUTATION_TO_BASE_ID_MAP = new ObjectIntIdentityMap();
    protected static final NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);
    protected static final NoiseGeneratorPerlin GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
    protected static final WorldGenDoublePlant DOUBLE_PLANT_GENERATOR = new WorldGenDoublePlant();
    /** The tree generator. */
    protected static final WorldGenTrees TREE_FEATURE = new WorldGenTrees(false);
    /** The big tree generator. */
    protected static final WorldGenBigTree BIG_TREE_FEATURE = new WorldGenBigTree(false);
    /** The swamp tree generator. */
    protected static final WorldGenSwamp SWAMP_FEATURE = new WorldGenSwamp();
    public static final RegistryNamespaced<ResourceLocation, Biome> REGISTRY = net.minecraftforge.fml.common.registry.GameData.getBiomeRegistry();
    private final String biomeName;
    /** The base height of this biome. Default 0.1. */
    private final float baseHeight;
    /** The variation from the base height of the biome. Default 0.3. */
    private final float heightVariation;
    /** The temperature of this biome. */
    private final float temperature;
    /** The rainfall in this biome. */
    private final float rainfall;
    /** Color tint applied to water depending on biome */
    private final int waterColor;
    /** Set to true if snow is enabled for this biome. */
    private final boolean enableSnow;
    /** Is true (default) if the biome support rain (desert and nether can't have rain) */
    private final boolean enableRain;
    /** The unique identifier of the biome for which this is a mutation of. */
    @Nullable
    private final String baseBiomeRegName;
    /** The block expected to be on the top of this biome */
    public IBlockState topBlock = Blocks.GRASS.getDefaultState();
    /** The block to fill spots in when not on the top */
    public IBlockState fillerBlock = Blocks.DIRT.getDefaultState();
    /** The biome decorator. */
    public BiomeDecorator theBiomeDecorator;
    /** Holds the classes of IMobs (hostile mobs) that can be spawned in the biome. */
    protected List<Biome.SpawnListEntry> spawnableMonsterList = Lists.<Biome.SpawnListEntry>newArrayList();
    /** Holds the classes of any creature that can be spawned in the biome as friendly creature. */
    protected List<Biome.SpawnListEntry> spawnableCreatureList = Lists.<Biome.SpawnListEntry>newArrayList();
    /** Holds the classes of any aquatic creature that can be spawned in the water of the biome. */
    protected List<Biome.SpawnListEntry> spawnableWaterCreatureList = Lists.<Biome.SpawnListEntry>newArrayList();
    protected List<Biome.SpawnListEntry> spawnableCaveCreatureList = Lists.<Biome.SpawnListEntry>newArrayList();

    public static int getIdForBiome(Biome biome)
    {
        return REGISTRY.getIDForObject(biome);
    }

    @Nullable
    public static Biome getBiomeForId(int id)
    {
        return (Biome)REGISTRY.getObjectById(id);
    }

    @Nullable
    public static Biome getMutationForBiome(Biome biome)
    {
        return (Biome)MUTATION_TO_BASE_ID_MAP.getByValue(getIdForBiome(biome));
    }

    public Biome(Biome.BiomeProperties properties)
    {
        this.biomeName = properties.biomeName;
        this.baseHeight = properties.baseHeight;
        this.heightVariation = properties.heightVariation;
        this.temperature = properties.temperature;
        this.rainfall = properties.rainfall;
        this.waterColor = properties.waterColor;
        this.enableSnow = properties.enableSnow;
        this.enableRain = properties.enableRain;
        this.baseBiomeRegName = properties.baseBiomeRegName;
        this.theBiomeDecorator = this.createBiomeDecorator();
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntitySheep.class, 12, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityPig.class, 10, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityChicken.class, 10, 4, 4));
        this.spawnableCreatureList.add(new Biome.SpawnListEntry(EntityCow.class, 8, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySpider.class, 100, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombie.class, 100, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntitySlime.class, 100, 4, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityWitch.class, 5, 1, 1));
        this.spawnableWaterCreatureList.add(new Biome.SpawnListEntry(EntitySquid.class, 10, 4, 4));
        this.spawnableCaveCreatureList.add(new Biome.SpawnListEntry(EntityBat.class, 10, 8, 8));
        this.addDefaultFlowers();
    }

    /**
     * Allocate a new BiomeDecorator for this BiomeGenBase
     */
    public BiomeDecorator createBiomeDecorator()
    {
        return getModdedBiomeDecorator(new BiomeDecorator());
    }

    public boolean isMutation()
    {
        return this.baseBiomeRegName != null;
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(rand.nextInt(10) == 0 ? BIG_TREE_FEATURE : TREE_FEATURE);
    }

    /**
     * Gets a WorldGen appropriate for this biome.
     */
    public WorldGenerator getRandomWorldGenForGrass(Random rand)
    {
        return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        return rand.nextInt(3) > 0 ? BlockFlower.EnumFlowerType.DANDELION : BlockFlower.EnumFlowerType.POPPY;
    }

    /**
     * takes temperature, returns color
     */
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature)
    {
        currentTemperature = currentTemperature / 3.0F;
        currentTemperature = MathHelper.clamp(currentTemperature, -1.0F, 1.0F);
        return MathHelper.hsvToRGB(0.62222224F - currentTemperature * 0.05F, 0.5F + currentTemperature * 0.1F, 1.0F);
    }

    /**
     * Returns the correspondent list of the EnumCreatureType informed.
     */
    public List<Biome.SpawnListEntry> getSpawnableList(EnumCreatureType creatureType)
    {
        switch (creatureType)
        {
            case MONSTER:
                return this.spawnableMonsterList;
            case CREATURE:
                return this.spawnableCreatureList;
            case WATER_CREATURE:
                return this.spawnableWaterCreatureList;
            case AMBIENT:
                return this.spawnableCaveCreatureList;
            default:
                return Collections.<Biome.SpawnListEntry>emptyList();
        }
    }

    /**
     * Returns true if the biome have snowfall instead a normal rain.
     */
    public boolean getEnableSnow()
    {
        return this.isSnowyBiome();
    }

    /**
     * Check if rain can occur in biome
     */
    public boolean canRain()
    {
        return this.isSnowyBiome() ? false : this.enableRain;
    }

    /**
     * Checks to see if the rainfall level of the biome is extremely high
     */
    public boolean isHighHumidity()
    {
        return this.getRainfall() > 0.85F;
    }

    /**
     * returns the chance a creature has to spawn.
     */
    public float getSpawningChance()
    {
        return 0.1F;
    }

    /**
     * Gets a floating point representation of this biome's temperature
     */
    public final float getFloatTemperature(BlockPos pos)
    {
        if (pos.getY() > 64)
        {
            float f = (float)(TEMPERATURE_NOISE.getValue((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F)) * 4.0D);
            return this.getTemperature() - (f + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.getTemperature();
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }

    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos pos)
    {
        double d0 = (double)MathHelper.clamp(this.getFloatTemperature(pos), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
        return getModdedBiomeGrassColor(ColorizerGrass.getGrassColor(d0, d1));
    }

    /**
     * Given x, z coordinates, we count down all the y positions starting at 255 and working our way down. When we hit a
     * non-air block, we replace it with this.topBlock (default grass, descendants may set otherwise), and then a
     * relatively shallow layer of blocks of type this.fillerBlock (default dirt). A random set of blocks below y == 5
     * (but always including y == 0) is replaced with bedrock.
     *  
     * If we don't hit non-air until somewhat below sea level, we top with gravel and fill down with stone.
     *  
     * If this.fillerBlock is red sand, we replace some of that with red sandstone.
     */
    public final void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        int i = worldIn.getSeaLevel();
        IBlockState iblockstate = this.topBlock;
        IBlockState iblockstate1 = this.fillerBlock;
        int j = -1;
        int k = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int l = x & 15;
        int i1 = z & 15;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int j1 = 255; j1 >= 0; --j1)
        {
            if (j1 <= rand.nextInt(5))
            {
                chunkPrimerIn.setBlockState(i1, j1, l, BEDROCK);
            }
            else
            {
                IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

                if (iblockstate2.getMaterial() == Material.AIR)
                {
                    j = -1;
                }
                else if (iblockstate2.getBlock() == Blocks.STONE)
                {
                    if (j == -1)
                    {
                        if (k <= 0)
                        {
                            iblockstate = AIR;
                            iblockstate1 = STONE;
                        }
                        else if (j1 >= i - 4 && j1 <= i + 1)
                        {
                            iblockstate = this.topBlock;
                            iblockstate1 = this.fillerBlock;
                        }

                        if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR))
                        {
                            if (this.getFloatTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F)
                            {
                                iblockstate = ICE;
                            }
                            else
                            {
                                iblockstate = WATER;
                            }
                        }

                        j = k;

                        if (j1 >= i - 1)
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
                        }
                        else if (j1 < i - 7 - k)
                        {
                            iblockstate = AIR;
                            iblockstate1 = STONE;
                            chunkPrimerIn.setBlockState(i1, j1, l, GRAVEL);
                        }
                        else
                        {
                            chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                        }
                    }
                    else if (j > 0)
                    {
                        --j;
                        chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

                        if (j == 0 && iblockstate1.getBlock() == Blocks.SAND && k > 1)
                        {
                            j = rand.nextInt(4) + Math.max(0, j1 - 63);
                            iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? RED_SANDSTONE : SANDSTONE;
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos pos)
    {
        double d0 = (double)MathHelper.clamp(this.getFloatTemperature(pos), 0.0F, 1.0F);
        double d1 = (double)MathHelper.clamp(this.getRainfall(), 0.0F, 1.0F);
        return getModdedBiomeFoliageColor(ColorizerFoliage.getFoliageColor(d0, d1));
    }

    public Class <? extends Biome > getBiomeClass()
    {
        return this.getClass();
    }

    public Biome.TempCategory getTempCategory()
    {
        return (double)this.getTemperature() < 0.2D ? Biome.TempCategory.COLD : ((double)this.getTemperature() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM);
    }

    /**
     * return the biome specified by biomeID, or 0 (ocean) if out of bounds
     */
    @Nullable
    public static Biome getBiome(int id)
    {
        return getBiome(id, (Biome)null);
    }

    public static Biome getBiome(int biomeId, Biome fallback)
    {
        Biome biome = getBiomeForId(biomeId);
        return biome == null ? fallback : biome;
    }

    public boolean ignorePlayerSpawnSuitability()
    {
        return false;
    }

    public final float getBaseHeight()
    {
        return this.baseHeight;
    }

    /**
     * Gets a floating point representation of this biome's rainfall
     */
    public final float getRainfall()
    {
        return this.rainfall;
    }

    public final String getBiomeName()
    {
        return this.biomeName;
    }

    public final float getHeightVariation()
    {
        return this.heightVariation;
    }

    public final float getTemperature()
    {
        return this.temperature;
    }

    @SideOnly(Side.CLIENT)
    public final int getWaterColor()
    {
        return this.waterColor;
    }

    public final boolean isSnowyBiome()
    {
        return this.enableSnow;
    }

    /* ========================================= FORGE START ======================================*/
    protected List<FlowerEntry> flowers = new java.util.ArrayList<FlowerEntry>();

    public BiomeDecorator getModdedBiomeDecorator(BiomeDecorator original)
    {
        return new net.minecraftforge.event.terraingen.DeferredBiomeDecorator(original);
    }

    public int getWaterColorMultiplier()
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor(this, waterColor);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    public int getModdedBiomeGrassColor(int original)
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetGrassColor(this, original);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    public int getModdedBiomeFoliageColor(int original)
    {
        net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor event = new net.minecraftforge.event.terraingen.BiomeEvent.GetFoliageColor(this, original);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        return event.getNewColor();
    }

    /**
     * Weighted random holder class used to hold possible flowers
     * that can spawn in this biome when bonemeal is used on grass.
     */
    public static class FlowerEntry extends WeightedRandom.Item
    {
        public final net.minecraft.block.state.IBlockState state;
        public FlowerEntry(net.minecraft.block.state.IBlockState state, int weight)
        {
            super(weight);
            this.state = state;
        }
    }

    /**
     * Adds the default flowers, as of 1.7, it is 2 yellow, and 1 red. I chose 10 to allow some wiggle room in the numbers.
     */
    public void addDefaultFlowers()
    {
        addFlower(Blocks.YELLOW_FLOWER.getDefaultState().withProperty(Blocks.YELLOW_FLOWER.getTypeProperty(), BlockFlower.EnumFlowerType.DANDELION), 20);
        addFlower(Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), BlockFlower.EnumFlowerType.POPPY), 20);
    }

    /** Register a new plant to be planted when bonemeal is used on grass.
     * @param state The block to place.
     * @param weight The weight of the plant, where red flowers are
     *               10 and yellow flowers are 20.
     */
    public void addFlower(IBlockState state, int weight)
    {
        this.flowers.add(new FlowerEntry(state, weight));
    }

    public void plantFlower(World world, Random rand, BlockPos pos)
    {
        FlowerEntry flower = (FlowerEntry)WeightedRandom.getRandomItem(rand, flowers);
        if (flower == null || flower.state == null ||
            (flower.state.getBlock() instanceof net.minecraft.block.BlockBush &&
              !((net.minecraft.block.BlockBush)flower.state.getBlock()).canBlockStay(world, pos, flower.state)))
        {
            return;
        }

        world.setBlockState(pos, flower.state, 3);
    }

    /* ========================================= FORGE END ======================================*/

    /**
     * Registers all of the vanilla biomes.
     */
    public static void registerBiomes()
    {
        registerBiome(0, "ocean", new BiomeOcean((new Biome.BiomeProperties("Ocean")).setBaseHeight(-1.0F).setHeightVariation(0.1F)));
        registerBiome(1, "plains", new BiomePlains(false, (new Biome.BiomeProperties("Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F)));
        registerBiome(2, "desert", new BiomeDesert((new Biome.BiomeProperties("Desert")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(3, "extreme_hills", new BiomeHills(BiomeHills.Type.NORMAL, (new Biome.BiomeProperties("Extreme Hills")).setBaseHeight(1.0F).setHeightVariation(0.5F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(4, "forest", new BiomeForest(BiomeForest.Type.NORMAL, (new Biome.BiomeProperties("Forest")).setTemperature(0.7F).setRainfall(0.8F)));
        registerBiome(5, "taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Taiga")).setBaseHeight(0.2F).setHeightVariation(0.2F).setTemperature(0.25F).setRainfall(0.8F)));
        registerBiome(6, "swampland", new BiomeSwamp((new Biome.BiomeProperties("Swampland")).setBaseHeight(-0.2F).setHeightVariation(0.1F).setTemperature(0.8F).setRainfall(0.9F).setWaterColor(14745518)));
        registerBiome(7, "river", new BiomeRiver((new Biome.BiomeProperties("River")).setBaseHeight(-0.5F).setHeightVariation(0.0F)));
        registerBiome(8, "hell", new BiomeHell((new Biome.BiomeProperties("Hell")).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(9, "sky", new BiomeEnd((new Biome.BiomeProperties("The End")).setRainDisabled()));
        registerBiome(10, "frozen_ocean", new BiomeOcean((new Biome.BiomeProperties("FrozenOcean")).setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()));
        registerBiome(11, "frozen_river", new BiomeRiver((new Biome.BiomeProperties("FrozenRiver")).setBaseHeight(-0.5F).setHeightVariation(0.0F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()));
        registerBiome(12, "ice_flats", new BiomeSnow(false, (new Biome.BiomeProperties("Ice Plains")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()));
        registerBiome(13, "ice_mountains", new BiomeSnow(false, (new Biome.BiomeProperties("Ice Mountains")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()));
        registerBiome(14, "mushroom_island", new BiomeMushroomIsland((new Biome.BiomeProperties("MushroomIsland")).setBaseHeight(0.2F).setHeightVariation(0.3F).setTemperature(0.9F).setRainfall(1.0F)));
        registerBiome(15, "mushroom_island_shore", new BiomeMushroomIsland((new Biome.BiomeProperties("MushroomIslandShore")).setBaseHeight(0.0F).setHeightVariation(0.025F).setTemperature(0.9F).setRainfall(1.0F)));
        registerBiome(16, "beaches", new BiomeBeach((new Biome.BiomeProperties("Beach")).setBaseHeight(0.0F).setHeightVariation(0.025F).setTemperature(0.8F).setRainfall(0.4F)));
        registerBiome(17, "desert_hills", new BiomeDesert((new Biome.BiomeProperties("DesertHills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(18, "forest_hills", new BiomeForest(BiomeForest.Type.NORMAL, (new Biome.BiomeProperties("ForestHills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(0.7F).setRainfall(0.8F)));
        registerBiome(19, "taiga_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("TaigaHills")).setTemperature(0.25F).setRainfall(0.8F).setBaseHeight(0.45F).setHeightVariation(0.3F)));
        registerBiome(20, "smaller_extreme_hills", new BiomeHills(BiomeHills.Type.EXTRA_TREES, (new Biome.BiomeProperties("Extreme Hills Edge")).setBaseHeight(0.8F).setHeightVariation(0.3F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(21, "jungle", new BiomeJungle(false, (new Biome.BiomeProperties("Jungle")).setTemperature(0.95F).setRainfall(0.9F)));
        registerBiome(22, "jungle_hills", new BiomeJungle(false, (new Biome.BiomeProperties("JungleHills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(0.95F).setRainfall(0.9F)));
        registerBiome(23, "jungle_edge", new BiomeJungle(true, (new Biome.BiomeProperties("JungleEdge")).setTemperature(0.95F).setRainfall(0.8F)));
        registerBiome(24, "deep_ocean", new BiomeOcean((new Biome.BiomeProperties("Deep Ocean")).setBaseHeight(-1.8F).setHeightVariation(0.1F)));
        registerBiome(25, "stone_beach", new BiomeStoneBeach((new Biome.BiomeProperties("Stone Beach")).setBaseHeight(0.1F).setHeightVariation(0.8F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(26, "cold_beach", new BiomeBeach((new Biome.BiomeProperties("Cold Beach")).setBaseHeight(0.0F).setHeightVariation(0.025F).setTemperature(0.05F).setRainfall(0.3F).setSnowEnabled()));
        registerBiome(27, "birch_forest", new BiomeForest(BiomeForest.Type.BIRCH, (new Biome.BiomeProperties("Birch Forest")).setTemperature(0.6F).setRainfall(0.6F)));
        registerBiome(28, "birch_forest_hills", new BiomeForest(BiomeForest.Type.BIRCH, (new Biome.BiomeProperties("Birch Forest Hills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(0.6F).setRainfall(0.6F)));
        registerBiome(29, "roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new Biome.BiomeProperties("Roofed Forest")).setTemperature(0.7F).setRainfall(0.8F)));
        registerBiome(30, "taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga")).setBaseHeight(0.2F).setHeightVariation(0.2F).setTemperature(-0.5F).setRainfall(0.4F).setSnowEnabled()));
        registerBiome(31, "taiga_cold_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga Hills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(-0.5F).setRainfall(0.4F).setSnowEnabled()));
        registerBiome(32, "redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new Biome.BiomeProperties("Mega Taiga")).setTemperature(0.3F).setRainfall(0.8F).setBaseHeight(0.2F).setHeightVariation(0.2F)));
        registerBiome(33, "redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA, (new Biome.BiomeProperties("Mega Taiga Hills")).setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(0.3F).setRainfall(0.8F)));
        registerBiome(34, "extreme_hills_with_trees", new BiomeHills(BiomeHills.Type.EXTRA_TREES, (new Biome.BiomeProperties("Extreme Hills+")).setBaseHeight(1.0F).setHeightVariation(0.5F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(35, "savanna", new BiomeSavanna((new Biome.BiomeProperties("Savanna")).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(1.2F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(36, "savanna_rock", new BiomeSavanna((new Biome.BiomeProperties("Savanna Plateau")).setBaseHeight(1.5F).setHeightVariation(0.025F).setTemperature(1.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(37, "mesa", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa")).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(38, "mesa_rock", new BiomeMesa(false, true, (new Biome.BiomeProperties("Mesa Plateau F")).setBaseHeight(1.5F).setHeightVariation(0.025F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(39, "mesa_clear_rock", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa Plateau")).setBaseHeight(1.5F).setHeightVariation(0.025F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(127, "void", new BiomeVoid((new Biome.BiomeProperties("The Void")).setRainDisabled()));
        registerBiome(129, "mutated_plains", new BiomePlains(true, (new Biome.BiomeProperties("Sunflower Plains")).setBaseBiome("plains").setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(0.8F).setRainfall(0.4F)));
        registerBiome(130, "mutated_desert", new BiomeDesert((new Biome.BiomeProperties("Desert M")).setBaseBiome("desert").setBaseHeight(0.225F).setHeightVariation(0.25F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(131, "mutated_extreme_hills", new BiomeHills(BiomeHills.Type.MUTATED, (new Biome.BiomeProperties("Extreme Hills M")).setBaseBiome("extreme_hills").setBaseHeight(1.0F).setHeightVariation(0.5F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(132, "mutated_forest", new BiomeForest(BiomeForest.Type.FLOWER, (new Biome.BiomeProperties("Flower Forest")).setBaseBiome("forest").setHeightVariation(0.4F).setTemperature(0.7F).setRainfall(0.8F)));
        registerBiome(133, "mutated_taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Taiga M")).setBaseBiome("taiga").setBaseHeight(0.3F).setHeightVariation(0.4F).setTemperature(0.25F).setRainfall(0.8F)));
        registerBiome(134, "mutated_swampland", new BiomeSwamp((new Biome.BiomeProperties("Swampland M")).setBaseBiome("swampland").setBaseHeight(-0.1F).setHeightVariation(0.3F).setTemperature(0.8F).setRainfall(0.9F).setWaterColor(14745518)));
        registerBiome(140, "mutated_ice_flats", new BiomeSnow(true, (new Biome.BiomeProperties("Ice Plains Spikes")).setBaseBiome("ice_flats").setBaseHeight(0.425F).setHeightVariation(0.45000002F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()));
        registerBiome(149, "mutated_jungle", new BiomeJungle(false, (new Biome.BiomeProperties("Jungle M")).setBaseBiome("jungle").setBaseHeight(0.2F).setHeightVariation(0.4F).setTemperature(0.95F).setRainfall(0.9F)));
        registerBiome(151, "mutated_jungle_edge", new BiomeJungle(true, (new Biome.BiomeProperties("JungleEdge M")).setBaseBiome("jungle_edge").setBaseHeight(0.2F).setHeightVariation(0.4F).setTemperature(0.95F).setRainfall(0.8F)));
        registerBiome(155, "mutated_birch_forest", new BiomeForestMutated((new Biome.BiomeProperties("Birch Forest M")).setBaseBiome("birch_forest").setBaseHeight(0.2F).setHeightVariation(0.4F).setTemperature(0.6F).setRainfall(0.6F)));
        registerBiome(156, "mutated_birch_forest_hills", new BiomeForestMutated((new Biome.BiomeProperties("Birch Forest Hills M")).setBaseBiome("birch_forest").setBaseHeight(0.55F).setHeightVariation(0.5F).setTemperature(0.6F).setRainfall(0.6F)));
        registerBiome(157, "mutated_roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED, (new Biome.BiomeProperties("Roofed Forest M")).setBaseBiome("roofed_forest").setBaseHeight(0.2F).setHeightVariation(0.4F).setTemperature(0.7F).setRainfall(0.8F)));
        registerBiome(158, "mutated_taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL, (new Biome.BiomeProperties("Cold Taiga M")).setBaseBiome("taiga_cold").setBaseHeight(0.3F).setHeightVariation(0.4F).setTemperature(-0.5F).setRainfall(0.4F).setSnowEnabled()));
        registerBiome(160, "mutated_redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new Biome.BiomeProperties("Mega Spruce Taiga")).setBaseBiome("redwood_taiga").setBaseHeight(0.2F).setHeightVariation(0.2F).setTemperature(0.25F).setRainfall(0.8F)));
        registerBiome(161, "mutated_redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE, (new Biome.BiomeProperties("Redwood Taiga Hills M")).setBaseBiome("redwood_taiga_hills").setBaseHeight(0.2F).setHeightVariation(0.2F).setTemperature(0.25F).setRainfall(0.8F)));
        registerBiome(162, "mutated_extreme_hills_with_trees", new BiomeHills(BiomeHills.Type.MUTATED, (new Biome.BiomeProperties("Extreme Hills+ M")).setBaseBiome("extreme_hills_with_trees").setBaseHeight(1.0F).setHeightVariation(0.5F).setTemperature(0.2F).setRainfall(0.3F)));
        registerBiome(163, "mutated_savanna", new BiomeSavannaMutated((new Biome.BiomeProperties("Savanna M")).setBaseBiome("savanna").setBaseHeight(0.3625F).setHeightVariation(1.225F).setTemperature(1.1F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(164, "mutated_savanna_rock", new BiomeSavannaMutated((new Biome.BiomeProperties("Savanna Plateau M")).setBaseBiome("savanna_rock").setBaseHeight(1.05F).setHeightVariation(1.2125001F).setTemperature(1.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(165, "mutated_mesa", new BiomeMesa(true, false, (new Biome.BiomeProperties("Mesa (Bryce)")).setBaseBiome("mesa").setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(166, "mutated_mesa_rock", new BiomeMesa(false, true, (new Biome.BiomeProperties("Mesa Plateau F M")).setBaseBiome("mesa_rock").setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        registerBiome(167, "mutated_mesa_clear_rock", new BiomeMesa(false, false, (new Biome.BiomeProperties("Mesa Plateau M")).setBaseBiome("mesa_clear_rock").setBaseHeight(0.45F).setHeightVariation(0.3F).setTemperature(2.0F).setRainfall(0.0F).setRainDisabled()));
        Collections.addAll(EXPLORATION_BIOMES_LIST, new Biome[] {Biomes.OCEAN, Biomes.PLAINS, Biomes.DESERT, Biomes.EXTREME_HILLS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMPLAND, Biomes.RIVER, Biomes.FROZEN_RIVER, Biomes.ICE_PLAINS, Biomes.ICE_MOUNTAINS, Biomes.MUSHROOM_ISLAND, Biomes.MUSHROOM_ISLAND_SHORE, Biomes.BEACH, Biomes.DESERT_HILLS, Biomes.FOREST_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.DEEP_OCEAN, Biomes.STONE_BEACH, Biomes.COLD_BEACH, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.ROOFED_FOREST, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS, Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS, Biomes.EXTREME_HILLS_WITH_TREES, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.MESA, Biomes.MESA_ROCK, Biomes.MESA_CLEAR_ROCK});
    }

    /**
     * Registers a new biome into the registry.
     */
    public static void registerBiome(int id, String name, Biome biome)
    {
        REGISTRY.register(id, new ResourceLocation(name), biome);

        if (biome.isMutation())
        {
            MUTATION_TO_BASE_ID_MAP.put(biome, getIdForBiome((Biome)REGISTRY.getObject(new ResourceLocation(biome.baseBiomeRegName))));
        }
    }

    public static class BiomeProperties
        {
            private final String biomeName;
            private float baseHeight = 0.1F;
            private float heightVariation = 0.2F;
            private float temperature = 0.5F;
            private float rainfall = 0.5F;
            private int waterColor = 16777215;
            private boolean enableSnow;
            private boolean enableRain = true;
            @Nullable
            private String baseBiomeRegName;

            public BiomeProperties(String nameIn)
            {
                this.biomeName = nameIn;
            }

            public Biome.BiomeProperties setTemperature(float temperatureIn)
            {
                if (temperatureIn > 0.1F && temperatureIn < 0.2F)
                {
                    throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
                }
                else
                {
                    this.temperature = temperatureIn;
                    return this;
                }
            }

            public Biome.BiomeProperties setRainfall(float rainfallIn)
            {
                this.rainfall = rainfallIn;
                return this;
            }

            public Biome.BiomeProperties setBaseHeight(float baseHeightIn)
            {
                this.baseHeight = baseHeightIn;
                return this;
            }

            public Biome.BiomeProperties setHeightVariation(float heightVariationIn)
            {
                this.heightVariation = heightVariationIn;
                return this;
            }

            public Biome.BiomeProperties setRainDisabled()
            {
                this.enableRain = false;
                return this;
            }

            public Biome.BiomeProperties setSnowEnabled()
            {
                this.enableSnow = true;
                return this;
            }

            public Biome.BiomeProperties setWaterColor(int waterColorIn)
            {
                this.waterColor = waterColorIn;
                return this;
            }

            public Biome.BiomeProperties setBaseBiome(String nameIn)
            {
                this.baseBiomeRegName = nameIn;
                return this;
            }
        }

    public static class SpawnListEntry extends WeightedRandom.Item
        {
            public Class <? extends EntityLiving > entityClass;
            public int minGroupCount;
            public int maxGroupCount;
            private final java.lang.reflect.Constructor<?> ctr;

            public SpawnListEntry(Class <? extends EntityLiving > entityclassIn, int weight, int groupCountMin, int groupCountMax)
            {
                super(weight);
                this.entityClass = entityclassIn;
                this.minGroupCount = groupCountMin;
                this.maxGroupCount = groupCountMax;

                java.lang.reflect.Constructor<?> tmp = null;
                try
                {
                    tmp = entityclassIn.getConstructor(World.class);
                }
                catch (NoSuchMethodException e)
                {
                    com.google.common.base.Throwables.propagate(e);
                }
                ctr = tmp;
            }

            public String toString()
            {
                return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
            }

            public EntityLiving newInstance(World world) throws Exception
            {
                return (EntityLiving)ctr.newInstance(world);
            }
        }

    public static enum TempCategory
    {
        OCEAN,
        COLD,
        MEDIUM,
        WARM;
    }
}