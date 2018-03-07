package net.minecraft.world;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldType
{
    /** List of world types. */
    public static WorldType[] WORLD_TYPES = new WorldType[16];
    /** Default world type. */
    public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
    /** Flat world type. */
    public static final WorldType FLAT = new WorldType(1, "flat");
    /** Large Biome world Type. */
    public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
    /** amplified world type */
    public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).setNotificationData();
    public static final WorldType CUSTOMIZED = new WorldType(4, "customized");
    public static final WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");
    /** Default (1.1) world type. */
    public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
    /** ID for this world type. */
    private final int worldTypeId;
    private final String worldType;
    /** The int version of the ChunkProvider that generated this world. */
    private final int generatorVersion;
    /** Whether this world type can be generated. Normally true; set to false for out-of-date generator versions. */
    private boolean canBeCreated;
    /** Whether this WorldType has a version or not. */
    private boolean isWorldTypeVersioned;
    private boolean hasNotificationData;

    private WorldType(int id, String name)
    {
        this(id, name, 0);
    }

    private WorldType(int id, String name, int version)
    {
        if (name.length() > 16 && DEBUG_WORLD != null) throw new IllegalArgumentException("World type names must not be longer then 16: " + name);
        this.worldType = name;
        this.generatorVersion = version;
        this.canBeCreated = true;
        this.worldTypeId = id;
        WORLD_TYPES[id] = this;
    }

    public String getName()
    {
        return this.worldType;
    }

    /**
     * Gets the translation key for the name of this world type.
     */
    @SideOnly(Side.CLIENT)
    public String getTranslateName()
    {
        return "generator." + this.worldType;
    }

    /**
     * Gets the translation key for the info text for this world type.
     */
    @SideOnly(Side.CLIENT)
    public String getTranslatedInfo()
    {
        return this.getTranslateName() + ".info";
    }

    /**
     * Returns generatorVersion.
     */
    public int getGeneratorVersion()
    {
        return this.generatorVersion;
    }

    public WorldType getWorldTypeForGeneratorVersion(int version)
    {
        return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
    }

    /**
     * Sets canBeCreated to the provided value, and returns this.
     */
    private WorldType setCanBeCreated(boolean enable)
    {
        this.canBeCreated = enable;
        return this;
    }

    /**
     * Gets whether this WorldType can be used to generate a new world.
     */
    @SideOnly(Side.CLIENT)
    public boolean canBeCreated()
    {
        return this.canBeCreated;
    }

    /**
     * Flags this world type as having an associated version.
     */
    private WorldType setVersioned()
    {
        this.isWorldTypeVersioned = true;
        return this;
    }

    /**
     * Returns true if this world Type has a version associated with it.
     */
    public boolean isVersioned()
    {
        return this.isWorldTypeVersioned;
    }

    public static WorldType parseWorldType(String type)
    {
        for (WorldType worldtype : WORLD_TYPES)
        {
            if (worldtype != null && worldtype.worldType.equalsIgnoreCase(type))
            {
                return worldtype;
            }
        }

        return null;
    }

    public int getWorldTypeID()
    {
        return this.worldTypeId;
    }

    /**
     * returns true if selecting this worldtype from the customize menu should display the generator.[worldtype].info
     * message
     */
    @SideOnly(Side.CLIENT)
    public boolean showWorldInfoNotice()
    {
        return this.hasNotificationData;
    }

    /**
     * enables the display of generator.[worldtype].info message on the customize world menu
     */
    private WorldType setNotificationData()
    {
        this.hasNotificationData = true;
        return this;
    }

    public net.minecraft.world.biome.BiomeProvider getBiomeProvider(World world)
    {
        if (this == FLAT)
        {
            net.minecraft.world.gen.FlatGeneratorInfo flatgeneratorinfo = net.minecraft.world.gen.FlatGeneratorInfo.createFlatGeneratorFromString(world.getWorldInfo().getGeneratorOptions());
            return new net.minecraft.world.biome.BiomeProviderSingle(net.minecraft.world.biome.Biome.getBiome(flatgeneratorinfo.getBiome(), net.minecraft.init.Biomes.DEFAULT));
        }
        else if (this == DEBUG_WORLD)
        {
            return new net.minecraft.world.biome.BiomeProviderSingle(net.minecraft.init.Biomes.PLAINS);
        }
        else
        {
            return new net.minecraft.world.biome.BiomeProvider(world.getWorldInfo());
        }
    }

    public net.minecraft.world.chunk.IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        if (this == FLAT) return new net.minecraft.world.gen.ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
        if (this == DEBUG_WORLD) return new net.minecraft.world.gen.ChunkProviderDebug(world);
        if (this == CUSTOMIZED) return new net.minecraft.world.gen.ChunkProviderOverworld(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
        return new net.minecraft.world.gen.ChunkProviderOverworld(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
    }

    public int getMinimumSpawnHeight(World world)
    {
        return this == FLAT ? 4 : world.getSeaLevel() + 1;
    }

    public double getHorizon(World world)
    {
        return this == FLAT ? 0.0D : 63.0D;
    }

    public double voidFadeMagnitude()
    {
        return this == FLAT ? 1.0D : 0.03125D;
    }

    public boolean handleSlimeSpawnReduction(java.util.Random random, World world)
    {
        return this == FLAT ? random.nextInt(4) != 1 : false;
    }

    /*=================================================== FORGE START ======================================*/
    private static int getNextID()
    {
        for (int x = 0; x < WORLD_TYPES.length; x++)
        {
            if (WORLD_TYPES[x] == null)
            {
                return x;
            }
        }

        int oldLen = WORLD_TYPES.length;
        WORLD_TYPES = java.util.Arrays.copyOf(WORLD_TYPES, oldLen + 16);
        return oldLen;
    }

    /**
     * Creates a new world type, the ID is hidden and should not be referenced by modders.
     * It will automatically expand the underlying workdType array if there are no IDs left.
     * @param name
     */
    public WorldType(String name)
    {
        this(getNextID(), name);
    }

    /**
     * Called when 'Create New World' button is pressed before starting game
     */
    public void onGUICreateWorldPress() { }

    /**
     * Gets the spawn fuzz for players who join the world.
     * Useful for void world types.
     * @return Fuzz for entity initial spawn in blocks.
     */
    public int getSpawnFuzz(WorldServer world, net.minecraft.server.MinecraftServer server)
    {
        return Math.max(0, server.getSpawnRadius(world));
    }

    /**
     * Called when the 'Customize' button is pressed on world creation GUI
     * @param mc The Minecraft instance
     * @param guiCreateWorld the createworld GUI
     */
    @SideOnly(Side.CLIENT)
    public void onCustomizeButton(net.minecraft.client.Minecraft mc, net.minecraft.client.gui.GuiCreateWorld guiCreateWorld)
    {
        if (this == WorldType.FLAT)
        {
            mc.displayGuiScreen(new net.minecraft.client.gui.GuiCreateFlatWorld(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
        }
        else if (this == WorldType.CUSTOMIZED)
        {
            mc.displayGuiScreen(new net.minecraft.client.gui.GuiCustomizeWorldScreen(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
        }
    }

    /**
     * Should world creation GUI show 'Customize' button for this world type?
     * @return if this world type has customization parameters
     */
    public boolean isCustomizable()
    {
        return this == FLAT || this == WorldType.CUSTOMIZED;
    }


    /**
     * Get the height to render the clouds for this world type
     * @return The height to render clouds at
     */
    public float getCloudHeight()
    {
        return 128.0F;
    }

    /**
     * Creates the GenLayerBiome used for generating the world with the specified ChunkProviderSettings JSON String
     * *IF AND ONLY IF* this WorldType == WorldType.CUSTOMIZED.
     *
     *
     * @param worldSeed The world seed
     * @param parentLayer The parent layer to feed into any layer you return
     * @param chunkProviderSettingsJson The JSON string to use when initializing ChunkProviderSettings.Factory
     * @return A GenLayer that will return ints representing the Biomes to be generated, see GenLayerBiome
     */
    public net.minecraft.world.gen.layer.GenLayer getBiomeLayer(long worldSeed, net.minecraft.world.gen.layer.GenLayer parentLayer, String chunkProviderSettingsJson)
    {
        net.minecraft.world.gen.layer.GenLayer ret = new net.minecraft.world.gen.layer.GenLayerBiome(200L, parentLayer, this, chunkProviderSettingsJson);
        ret = net.minecraft.world.gen.layer.GenLayerZoom.magnify(1000L, ret, 2);
        ret = new net.minecraft.world.gen.layer.GenLayerBiomeEdge(1000L, ret);
        return ret;
    }
}