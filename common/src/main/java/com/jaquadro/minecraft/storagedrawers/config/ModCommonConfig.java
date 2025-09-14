package com.jaquadro.minecraft.storagedrawers.config;

import com.jaquadro.minecraft.storagedrawers.api.config.IDrawerConfig;
import com.texelsaurus.minecraft.chameleon.ChameleonServices;
import com.texelsaurus.minecraft.chameleon.config.ConfigSpec;
import com.texelsaurus.minecraft.chameleon.service.ChameleonConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ModCommonConfig extends ConfigSpec
{
    public static ModCommonConfig INSTANCE = new ModCommonConfig();

    private final ChameleonConfig commonConfig;
    public General GENERAL;
    public Drawers DRAWERS;
    public Controller CONTROLLER;
    public Upgrades UPGRADES;
    public Tools TOOLS;
    public Integration INTEGRATION;

    private ModCommonConfig () {
        commonConfig = ChameleonServices.CONFIG.create(this);
    }

    public ChameleonConfig context() {
        return commonConfig;
    }

    @Override
    public void init() {
        GENERAL = new General();
        DRAWERS = new Drawers();
        CONTROLLER = new Controller();
        UPGRADES = new Upgrades();
        TOOLS = new Tools();
        INTEGRATION = new Integration();
    }

    class ConfigSection {
        protected final String name;
        protected final String[] comment;

        public ConfigSection (String name, String... comment) {
            this.name = name;
            this.comment = comment;
        }

        public ConfigSection build () {
            if (comment != null && comment.length > 0)
                commonConfig.comment(comment);
            commonConfig.pushGroup(name);

            buildEntries();

            commonConfig.popGroup();
            return this;
        }

        protected void buildEntries () { }
    }

    public class General {
        public ChameleonConfig.ConfigEntry<Boolean> debugTrace;
        public ChameleonConfig.ConfigEntry<Boolean> enableUI;
        public ChameleonConfig.ConfigEntry<Boolean> logStartupActivity;
        public ChameleonConfig.ConfigEntry<Integer> configVersion;

        public General() {
            commonConfig.comment("General mod configuration");
            commonConfig.pushGroup("General");

            debugTrace = commonConfig.define("debugTrace", false)
                .comment("Enables excessive logging around certain parts of the mod.",
                    "Can be ignored unless asked by the developer to enable.").build();

            enableUI = commonConfig.define("enableUI", true)
                .comment("", "Whether blocks with UI components are allowed to open them.",
                    "Disabling UI will restrict some mod functionality.").build();

            logStartupActivity = commonConfig.define("logStartupActivity", true)
                .comment("", "Whether to log actions such as adding rules or deny list entries.",
                    "You may wish to disable this if you've added many such entries.").build();

            configVersion = commonConfig.define("configVersion", 2)
                .comment("", "Internal use to record what version the config file was first written with.").build();

            commonConfig.popGroup();
        }
    }

    public class Drawers
    {
        public class DrawerConfig extends ConfigSection implements IDrawerConfig
        {
            public ChameleonConfig.ConfigEntry<Integer> unitsPerSlot;

            public DrawerConfig (String name, int unitsPerSlot, String... comment) {
                super(name, comment);

                this.unitsPerSlot = commonConfig.define("unitsPerSlot", unitsPerSlot);
            }

            @Override
            protected void buildEntries () {
                unitsPerSlot.build();
            }

            @Override
            public DrawerConfig build () {
                super.build();
                return this;
            }

            @Override
            public int getUnitsPerSlot () {
                if (!isLoaded())
                    return 1;

                return unitsPerSlot.get();
            }
        }

        public class Compacting extends ConfigSection {
            public ChameleonConfig.ConfigEntry<Boolean> enabled;
            public ChameleonConfig.ConfigEntry<Boolean> enableExtraCompactingRules;
            public ChameleonConfig.ConfigEntry<List<? extends String>> compRules;

            public Compacting (String name, String... comment) {
                super(name, comment);

                this.enabled = commonConfig.define("enabled", true)
                    .comment("Whether compacting function is supported.  Disabling does not remove the block.");

                enableExtraCompactingRules = commonConfig.define("enableExtraCompactingRules", true)
                    .comment("", "Enables additional rules for some Minecraft items, like quartz, brick, and clay.",
                        "See logs for full set of rules that get added.");

                compRules = commonConfig.defineList("compactingRules", Arrays.asList(
                        "minecraft:clay, minecraft:clay_ball, 4"), null)
                    .comment("", "Compacting drawers require 2-way 2x2 or 3x3 recipes to associate items.",
                        "Add additional rules here in the form: <big_item>, <small_item>, <amount>");
            }

            @Override
            protected void buildEntries () {
                enabled.build();
                enableExtraCompactingRules.build();
                compRules.build();
            }

            @Override
            public Compacting build () {
                super.build();
                return this;
            }
        }

        public class Filled extends ConfigSection {
            public ChameleonConfig.ConfigEntry<Boolean> heavyDrawers;
            public ChameleonConfig.ConfigEntry<Boolean> canStoreInDrawers;
            public ChameleonConfig.ConfigEntry<Boolean> canStoreInContainers;

            public Filled (String name, String... comment) {
                super(name, comment);

                heavyDrawers = commonConfig.define("heavyDrawers", false)
                    .comment("If enabled, carrying filled drawers in your inventory gives slowness debuff.",
                        "Debuff can be mitigated with portability upgrade, if it's enabled.");

                canStoreInDrawers = commonConfig.define("canStoreInDrawers", false)
                    .comment("", "Allows filled drawers to be stored in other drawer blocks.");

                canStoreInContainers = commonConfig.define("canStoreInContainers", false)
                    .comment("", "Allows filled drawers to be stored in containers like bundles and shulker boxes.",
                        "Due to current limitation, this rule will also apply to storing empty drawers in those containers.");
            }

            @Override
            protected void buildEntries () {
                heavyDrawers.build();
                canStoreInDrawers.build();
                canStoreInContainers.build();
            }

            @Override
            public Filled build () {
                super.build();
                return this;
            }
        }

        public class Detached extends ConfigSection {
            public ChameleonConfig.ConfigEntry<Boolean> enable;
            public ChameleonConfig.ConfigEntry<Boolean> heavyDrawers;
            public ChameleonConfig.ConfigEntry<Boolean> forceMaxCapacityCheck;
            public ChameleonConfig.ConfigEntry<Boolean> canStoreInContainers;

            public Detached (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true)
                    .comment("Allows individual drawer slots to be pulled out from a drawer block.");

                heavyDrawers = commonConfig.define("heavyDrawers", false)
                    .comment("", "If enabled, carrying filled drawers in your inventory gives slowness debuff.",
                        "Debuff can be mitigated with portability upgrade, if it's enabled.");

                forceMaxCapacityCheck = commonConfig.define("forceMaxCapacityCheck", false)
                    .comment("", "Drawers track the capacity upgrades from the block they were taken from.",
                        "When enabled, drawers can only be placed back into a block with the same or lower max capacity.",
                        "Drawers can still only be inserted into a block with enough capacity for the items held.");

                canStoreInContainers = commonConfig.define("canStoreInContainers", false)
                    .comment("", "Allows detached drawers to be stored in containers like bundles and shulker boxes.",
                        "Due to current limitation, this rule will also apply to storing empty drawers in those containers.");
            }

            @Override
            protected void buildEntries () {
                enable.build();
                heavyDrawers.build();
                forceMaxCapacityCheck.build();
                canStoreInContainers.build();
            }

            @Override
            public Detached build () {
                super.build();
                return this;
            }
        }

        public class Framed extends ConfigSection {
            public ChameleonConfig.ConfigEntry<Boolean> enable;
            public ChameleonConfig.ConfigEntry<Boolean> enforceSolidMaterials;
            public ChameleonConfig.ConfigEntry<Boolean> enforceOpaqueMaterials;
            public ChameleonConfig.ConfigEntry<List<? extends String>> materialBlacklist;

            public Framed (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true)
                    .comment("Allows crafting framed drawers.  Disabling does not remove existing framed drawers.");

                enforceSolidMaterials = commonConfig.define("enforceSolidMaterials", true)
                    .comment("", "Attempts to only allow solid, full-cube blocks to be used as materials.",
                        "This check may still allow non-solid blocks if the blocks' properties indicate they should be solid but are not.",
                        "Some non-solid blocks are also counted as non-opaque, and may be restricted by that setting as well.");

                enforceOpaqueMaterials = commonConfig.define("enforceOpaqueMaterials", false)
                    .comment("", "Attempts to only allow fully opaque blocks to be used as materials.",
                        "This check may still allow non-opaque blocks if the blocks' properties indicate they should be opaque but are not.");

                materialBlacklist = commonConfig.defineList("materialDenyList", new ArrayList<String>(), null)
                    .comment("", "Each entry should be a namespace or fully namespaced block, e.g. minecraft:cobblestone",
                        "Any items on the deny list are prevented from being used as any drawer material.");
            }

            @Override
            protected void buildEntries () {
                enable.build();
                enforceSolidMaterials.build();
                enforceOpaqueMaterials.build();
                materialBlacklist.build();
            }

            @Override
            public Framed build () {
                super.build();
                return this;
            }
        }

        public class Storage extends ConfigSection {
            public ChameleonConfig.ConfigEntry<List<? extends String>> storeBlacklist;
            public ChameleonConfig.ConfigEntry<DropMode> dropMode;
            public ChameleonConfig.ConfigEntry<Integer> dropStackLimit;

            public Storage (String name, String... comment) {
                super(name, comment);

                storeBlacklist = commonConfig.defineList("storeDenyList", Arrays.asList("storagedrawers:creative_vending_upgrade"), null)
                    .comment("", "Each entry should be a namespace or fully namespaced item, e.g. minecraft:cobblestone",
                        "Any items on the deny list are prevented from being stored in drawers");

                dropMode = commonConfig.defineEnum("dropMode", DropMode.KEEP)
                    .comment("", "What a drawer should do with its items when broken.  Acceptable values are:",
                        "KEEP: Drawer retains all items in the dropped item, like shulker boxes.  This is the default behavior.",
                        "DROP: Drawer drops items into the world, up to the dropStackLimit value.  Stored items over that limit are voided.",
                        "VOID: Drawer voids all items when dropped.");

                dropStackLimit = commonConfig.define("dropStackLimit", 32)
                    .comment("", "If dropMode is set to DROP, this is the maximum number of stacks that can be spilled into the world",
                        "from the entire block.  By default, this is set to the amount a vanilla chest could drop.");
            }

            @Override
            protected void buildEntries () {
                storeBlacklist.build();
                dropMode.build();
                dropStackLimit.build();
            }

            @Override
            public Storage build () {
                super.build();
                return this;
            }
        }

        public DrawerConfig fullDrawers1x1;
        public DrawerConfig fullDrawers1x2;
        public DrawerConfig fullDrawers2x2;
        public DrawerConfig halfDrawers1x1;
        public DrawerConfig halfDrawers1x2;
        public DrawerConfig halfDrawers2x2;
        public DrawerConfig fullCompacting;
        public DrawerConfig halfCompacting;

        public ChameleonConfig.ConfigEntry<Integer> baseStackStorage;

        public Compacting compacting;
        public Filled filled;
        public Detached detached;
        public Framed framed;
        public Storage storage;

        public Drawers () {
            commonConfig.comment("Configuration around drawer storage blocks");
            commonConfig.pushGroup("Drawers");

            baseStackStorage = commonConfig.define("baseStackStorage", 1)
                .comment("A base value multiplied against the storage size of all drawer slots.",
                    "Change this to uniformly increase all storage amounts").build();

            commonConfig.comment("Configuration for individual drawer configurations.",
                "Units are the number of stacks a slot holds before the base stack storage value is multiplied.");
            commonConfig.pushGroup("Blocks");

            fullDrawers1x1 = new DrawerConfig("FullStorage1x1", 32).build();
            fullDrawers1x2 = new DrawerConfig("FullStorage1x2", 16).build();
            fullDrawers2x2 = new DrawerConfig("FullStorage2x2", 8).build();
            fullCompacting = new DrawerConfig("FullCompacting", 32).build();
            halfDrawers1x1 = new DrawerConfig("HalfStorage1x1", 16).build();
            halfDrawers1x2 = new DrawerConfig("HalfStorage1x2", 8).build();
            halfDrawers2x2 = new DrawerConfig("HalfStorage2x2", 4).build();
            halfCompacting = new DrawerConfig("HalfCompacting", 16).build();

            commonConfig.popGroup();

            compacting = new Compacting("Compacting",
                "Configuration around the auto compacting feature").build();

            detached = new Detached("Detached",
                "Configuration around detached drawers, which are individual drawer slots pulled from a drawer block.").build();

            filled = new Filled("Filled",
                "Configuration around dropped drawer blocks that still have items inside them.").build();

            framed = new Framed("Framed",
                "Configuration around framed drawers, which take their appearance from other blocks used as materials.").build();

            storage = new Storage("Storage",
                "Configuration around storage of items.").build();

            commonConfig.popGroup();
        }

        public boolean anyHeavyDrawers() {
            return filled.heavyDrawers.get() || detached.heavyDrawers.get();
        }

        public int getBaseStackStorage() {
            return isLoaded() ? baseStackStorage.get() : 1;
        }
    }

    public class Controller {
        public ChameleonConfig.ConfigEntry<Integer> controllerRange;

        public Controller () {
            commonConfig.comment("Configuration around the controller and drawer networks.");
            commonConfig.pushGroup("Controller");

            controllerRange = commonConfig.define("controllerRange", 50)
                .comment("Controller range defines how far away a drawer can be connected on X, Y, or Z planes.",
                    "The largest recommended range is around 75, or the expected chunk load distance.",
                    "If setting this value higher, drawers will be unavailable if their chunks become unloaded.")
                .build();

            commonConfig.popGroup();
        }
    }

    public class Upgrades {
        public class Upgrade extends ConfigSection {
            public final ChameleonConfig.ConfigEntry<Boolean> enableUpgrade;

            public Upgrade (String name, String... comment) {
                super(name, comment);

                enableUpgrade = commonConfig.define("enableUpgrade", true);
            }

            @Override
            protected void buildEntries () {
                enableUpgrade.build();
            }

            @Override
            public Upgrade build () {
                super.build();
                return this;
            }
        }

        public class StorageTierUpgrade extends Upgrade {
            public final ChameleonConfig.ConfigEntry<Integer> storageMult;

            public StorageTierUpgrade (String upgradeName, int defaultMult, String... comment) {
                super(upgradeName, comment);

                storageMult = commonConfig.define("storageMultiplier", defaultMult);
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                storageMult.build();
            }

            @Override
            public StorageTierUpgrade build () {
                super.build();
                return this;
            }
        }

        public class ConversionUpgrade extends Upgrade {
            public final ChameleonConfig.ConfigEntry<List<? extends String>> oreTypes;
            public final ChameleonConfig.ConfigEntry<List<? extends String>> oreMaterials;
            public final ChameleonConfig.ConfigEntry<List<? extends String>> tagWhitelist;
            public final ChameleonConfig.ConfigEntry<List<? extends String>> tagBlacklist;
            public final ChameleonConfig.ConfigEntry<List<? extends String>> itemEquivGroups;

            public ConversionUpgrade (String upgradeName, String... comment) {
                super(upgradeName, comment);

                oreTypes = commonConfig.defineList("oreTypeAllowList", Arrays.asList(
                        "c:storage_blocks", "c:ingots", "c:nuggets"), null)
                    .comment("", "Each type will be combined with each material to create a set of allow list entries.",
                        "This is mainly a convenience for common ore-based materials.");

                oreMaterials = commonConfig.defineList("oreMaterialAllowList", Arrays.asList(
                        "aluminum", "constantan", "steel", "uranium", "invar", "tin", "lead", "silver", "platinum", "nickel", "osmium", "bronze", "electrum"), null)
                    .comment("", "Each type will be combined with each material to create a set of allow list entries.",
                        "This is mainly a convenience for common ore-based materials.");

                tagWhitelist = commonConfig.defineList("tagAllowList", new ArrayList<String>(), null)
                    .comment("", "Each allow list entry should be a fully namespaced tag, e.g. c:ingots/copper");

                tagBlacklist = commonConfig.defineList("tagDenyList", new ArrayList<String>(), null)
                    .comment("", "Each deny list entry should be a fully namespaced tag, e.g. c:ingots/copper.",
                        "All items not on the allow list are denied implicitly.  This can be used to exclude",
                        "specific entries created from the ore allow list set.");

                itemEquivGroups = commonConfig.defineList("itemEquivalenceGroups", new ArrayList<String>(), null)
                    .comment("", "Each entry is a semicolon-separated list of fully-namespaced items. All items within the",
                        "same entry are considered equivalent and convertible/interchangeable.",
                        "Example: [\"thermal:nickel_ore;immersiveengineering:ore_nickel\"]");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                oreTypes.build();
                oreMaterials.build();
                tagWhitelist.build();
                tagBlacklist.build();
                itemEquivGroups.build();
            }

            @Override
            public ConversionUpgrade build () {
                super.build();
                return this;
            }
        }

        public class IlluminationUpgrade extends Upgrade {
            public final ChameleonConfig.ConfigEntry<Integer> illuminationLevel;
            public final ChameleonConfig.ConfigEntry<Integer> minIlluminationLevel;

            public IlluminationUpgrade (String upgradeName, String... comment) {
                super(upgradeName, comment);

                illuminationLevel = commonConfig.defineInRange("illuminationLevel", 13, 0, 15)
                    .comment("", "Renders labels at minimum light level between 0 - 15");

                minIlluminationLevel = commonConfig.defineInRange("minIlluminationLevel", 1, 0, 15)
                    .comment("", "Renders labels without upgrade at minimum light level between 0 - 15");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                illuminationLevel.build();
                minIlluminationLevel.build();
            }

            @Override
            public IlluminationUpgrade build () {
                super.build();
                return this;
            }
        }

        public class RedstoneUpgrade extends Upgrade
        {
            public final ChameleonConfig.ConfigEntry<Boolean> enableMin;
            public final ChameleonConfig.ConfigEntry<Boolean> enableMax;
            public final ChameleonConfig.ConfigEntry<Boolean> analogOutput;

            public RedstoneUpgrade (String upgradeName, String... comment) {
                super(upgradeName, comment);

                enableMin = commonConfig.define("enableMinUpgrade", true)
                    .comment("", "Min redstone upgrades output the minimum signal of all drawers in block.");

                enableMax = commonConfig.define("enableMaxUpgrade", true)
                    .comment("", "Max redstone upgrades output the maximum signal of all drawers in block.");

                analogOutput = commonConfig.define("analogOutput", true)
                    .comment("", "Whether redstone upgrades should emit an analog redstone signal,",
                        "requiring the use of a comparator to read it.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                enableMin.build();
                enableMax.build();
                analogOutput.build();
            }

            @Override
            public RedstoneUpgrade build () {
                super.build();
                return this;
            }
        }

        public class RemoteUpgrade extends Upgrade
        {
            public final ChameleonConfig.ConfigEntry<Boolean> enableGroup;
            public final ChameleonConfig.ConfigEntry<Integer> maxRange;
            public final ChameleonConfig.ConfigEntry<Integer> maxGroupRange;

            public RemoteUpgrade (String upgradeName, String... comment) {
                super(upgradeName, comment);

                enableGroup = commonConfig.define("enableGroupUpgrade", true)
                    .comment("", "Group variant connects all drawers connected to the upgraded block.");

                maxRange = commonConfig.define("maxRange", 0)
                    .comment("", "Sets the max range of the single-variant remote upgrade.",
                        "The range is capped by the controller range.  Set to 0 to match controller range.");

                maxGroupRange = commonConfig.define("maxGroupRange", 0)
                    .comment("", "Sets the max range of the group-variant remote upgrade.",
                        "The range is capped by the controller range.  Set to 0 to match controller range.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                enableGroup.build();
                maxRange.build();
                maxGroupRange.build();
            }

            @Override
            public RemoteUpgrade build () {
                super.build();
                return this;
            }
        }

        public class MagnetTierUpgrade extends Upgrade {
            public final ChameleonConfig.ConfigEntry<List<? extends Integer>> range;
            public final ChameleonConfig.ConfigEntry<Integer> activeSpeed;
            public final ChameleonConfig.ConfigEntry<Integer> idleSpeed;

            public MagnetTierUpgrade (String upgradeName, List<Integer> defaultRange, int defaultSpeed, String... comment) {
                super(upgradeName, comment);

                range = commonConfig.defineList("range", defaultRange, null)
                    .comment("", "Range is blocks out from drawer as: [horizontal, up, down]");

                activeSpeed = commonConfig.define("activeSpeed", defaultSpeed)
                    .comment("", "Ticks between active collection when this is the highest upgrade tier.");

                idleSpeed = commonConfig.define("idleSpeed", 20)
                    .comment("", "Ticks between collection checks when this is the highest upgrade tier.",
                        "Collection is idle when items have not been collected within the last idleSpeed interval.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                range.build();
                activeSpeed.build();
                idleSpeed.build();
            }

            @Override
            public MagnetTierUpgrade build () {
                super.build();
                return this;
            }
        }

        public class MagnetUpgrade extends ConfigSection
        {
            public final ChameleonConfig.ConfigEntry<Boolean> additiveRange;
            public final ChameleonConfig.ConfigEntry<List<? extends Integer>> maxRange ;

            public final MagnetTierUpgrade tier1;
            public final MagnetTierUpgrade tier2;
            public final MagnetTierUpgrade tier3;

            public MagnetUpgrade (String upgradeName, String... comment) {
                super(upgradeName, comment);

                additiveRange = commonConfig.define("additiveRange", true)
                    .comment("", "When multiple magnet upgrades are used, their ranges are added together.");

                maxRange = commonConfig.defineList("maxRange", Arrays.asList(24, 8, 0), null)
                    .comment("", "Range is blocks out from drawer as: [horizontal, up, down]",
                        "If ranges from multiple upgrades are added, they are not allowed to exceed these values.");

                tier1 = new MagnetTierUpgrade("Level1", Arrays.asList(1, 1, 0), 20);
                tier2 = new MagnetTierUpgrade("Level2", Arrays.asList(4, 2, 0), 10);
                tier3 = new MagnetTierUpgrade("Level3", Arrays.asList(8, 3, 0), 5);
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                additiveRange.build();
                maxRange.build();
                tier1.build();
                tier2.build();
                tier3.build();
            }

            @Override
            public MagnetUpgrade build () {
                super.build();
                return this;
            }
        }

        public final StorageTierUpgrade obsidianStorage;
        public final StorageTierUpgrade copperStorage;
        public final StorageTierUpgrade ironStorage;
        public final StorageTierUpgrade goldStorage;
        public final StorageTierUpgrade emeraldStorage;
        public final StorageTierUpgrade diamondStorage;
        public final StorageTierUpgrade netheriteStorage;

        public final ConversionUpgrade conversionUpgrade;
        public final Upgrade creativeStorageUpgrade;
        public final Upgrade creativeVendingUpgrade;
        public final Upgrade balanceUpgrade;
        public final Upgrade fillLevelUpgrade;
        public final IlluminationUpgrade illuminationUpgrade;
        public final Upgrade hopperUpgrade;
        public final MagnetUpgrade magnetUpgrade;
        public final Upgrade oneStackUpgrade;
        public final Upgrade portabilityUpgrade;
        public final RedstoneUpgrade redstoneUpgrade;
        public final RemoteUpgrade remoteUpgrade;
        public final Upgrade voidUgrade;

        public Upgrades() {
            commonConfig.comment("Configuration around upgrade items that can be inserted into drawer blocks.");
            commonConfig.pushGroup("Upgrades");

            commonConfig.comment("Storage upgrades multiply storage capacity by the given amount.",
                "When multiple storage upgrades are used together, their multipliers are added before being applied.");
            commonConfig.pushGroup("StorageTiers");

            obsidianStorage = new StorageTierUpgrade("Obsidian", 2).build();
            copperStorage = new StorageTierUpgrade("Copper", 4).build();
            ironStorage = new StorageTierUpgrade("Iron", 8).build();
            goldStorage = new StorageTierUpgrade("Gold", 16).build();
            emeraldStorage = new StorageTierUpgrade("Emerald", 64).build();
            diamondStorage = new StorageTierUpgrade("Diamond", 256).build();
            netheriteStorage = new StorageTierUpgrade("Netherite", 2048).build();

            commonConfig.popGroup();

            balanceUpgrade = new Upgrade("Balance",
                "Allows same-item slots to balance out their amounts when items are added or removed from a slot.",
                "Works across networks when acting through a controller.").build();

            conversionUpgrade = new ConversionUpgrade("Conversion",
                "Allows the automatic conversion of same-type items based on configuration.").build();

            creativeStorageUpgrade = new Upgrade("CreativeStorage",
                "Allows storing ~2.1 billion (MAX_INT) items in each drawer slot.").build();

            creativeVendingUpgrade = new Upgrade("CreativeVending",
                "Allows vending infinite amounts of the items in each drawer slot.").build();

            fillLevelUpgrade = new Upgrade("FillLevel",
                "Adds fill bars to the face of drawers.").build();

            illuminationUpgrade = new IlluminationUpgrade("Illumination",
                "Renders drawer labels brighter than surrounding environment would allow.").build();

            hopperUpgrade = new Upgrade("Hopper",
                "Collects matching items through its top like a vanilla hopper.").build();

            magnetUpgrade = new MagnetUpgrade("Magnet",
                "Collects nearby matching items by teleporting them instantly to the drawer").build();

            oneStackUpgrade = new Upgrade("OneStack",
                "Restricts capacity of drawer to one stack.").build();

            portabilityUpgrade = new Upgrade("Portability",
                "Allows drawers with contents to be freely carried when heavy drawers is enabled.").build();

            redstoneUpgrade = new RedstoneUpgrade("Redstone",
                "Lets drawers emit redstone signals based on their fill level.").build();

            remoteUpgrade = new RemoteUpgrade("Remote",
                "Lets drawers connect to a controller wirelessly.").build();

            voidUgrade = new Upgrade("Void",
                "Causes drawers to accept but void compatible items when they are filled to capacity.").build();

            commonConfig.popGroup();
        }

        public StorageTierUpgrade getStorageUpgrade(int level) {
            return switch (level) {
                case 1 -> obsidianStorage;
                case 2 -> copperStorage;
                case 3 -> ironStorage;
                case 4 -> goldStorage;
                case 5 -> emeraldStorage;
                case 6 -> diamondStorage;
                case 7 -> netheriteStorage;
                default -> obsidianStorage;
            };
        }

        public int getLevelMult(int level) {
            if (!isLoaded())
                return 1;

            return switch (level) {
                case 1 -> obsidianStorage.storageMult.get();
                case 2 -> copperStorage.storageMult.get();
                case 3 -> ironStorage.storageMult.get();
                case 4 -> goldStorage.storageMult.get();
                case 5 -> emeraldStorage.storageMult.get();
                case 6 -> diamondStorage.storageMult.get();
                case 7 -> netheriteStorage.storageMult.get();
                default -> 1;
            };
        }
    }

    public class Tools {
        public class Key extends ConfigSection {
            public final ChameleonConfig.ConfigEntry<Boolean> enable;

            public Key (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true);
            }

            @Override
            protected void buildEntries () {
                enable.build();
            }

            @Override
            public Key build () {
                super.build();
                return this;
            }
        }

        public class QuantifyKey extends Key {
            public final ChameleonConfig.ConfigEntry<Boolean> showDefault;

            public QuantifyKey (String name, String... comment) {
                super(name, comment);

                showDefault = commonConfig.define("showDefault", false)
                    .comment("", "Show labels by default on newly placed drawers.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                showDefault.build();
            }

            @Override
            public QuantifyKey build () {
                super.build();
                return this;
            }
        }

        public Key drawerKey;
        public QuantifyKey quantifyKey;
        public Key concealmentKey;
        public Key personalKey;
        public Key priorityKey;
        public Key suspendKey;

        public Tools () {
            commonConfig.comment("Configuration around tools, namely the various 'keys' that can be used on drawers.");
            commonConfig.pushGroup("Tools");

            drawerKey = new Key("DrawerKey",
                "Drawer keys are used to lock drawers to the items they already hold.").build();

            quantifyKey = new QuantifyKey("QuantifyKey",
                "Quantify keys are used to show or hide the count of items on the face of drawers.").build();

            concealmentKey = new Key("ConcealmentKey",
                "Concealment keys are used to show or hide the item labels on the face of drawers.",
                "The primary use of this key is for performance by disabling the more expensive dynamic rendering.").build();

            personalKey = new Key("PersonalKey",
                "Personal keys allow drawers to be locked to their owners, so only they can place or take items.").build();

            priorityKey = new Key("PriorityKey",
                "Priority keys change the priority of drawers when finding a compatible slot to insert items into.").build();

            suspendKey = new Key("PauseKey",
                "Suspend keys stop external interaction, e.g. from hopper or magnet upgrades.").build();

            commonConfig.popGroup();
        }
    }

    public class Integration {

        public class Waila extends ConfigSection {
            public final ChameleonConfig.ConfigEntry<Boolean> enable;
            public final ChameleonConfig.ConfigEntry<Boolean> stackRemainder;
            public final ChameleonConfig.ConfigEntry<Boolean> respectQuantifyKey;

            public Waila (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true)
                    .comment("Enables Jade integration if mod is present.");

                stackRemainder = commonConfig.define("stackRemainder", true)
                    .comment("When true, shows quantity as NxS + R (by stack size) rather than count");

                respectQuantifyKey = commonConfig.define("respectQuantifyKey", false)
                    .comment("", "When true, does not show current quantities unless quantify key was used");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                enable.build();
                stackRemainder.build();
                respectQuantifyKey.build();
            }

            @Override
            public Waila build () {
                super.build();
                return this;
            }
        }

        public class FTBChunks extends ConfigSection {
            public final ChameleonConfig.ConfigEntry<Boolean> enable;

            public FTBChunks (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true)
                    .comment("Enables FTB Chunks integration if mod is present.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                enable.build();
            }

            @Override
            public FTBChunks build () {
                super.build();
                return this;
            }
        }

        public class FTBTeams extends ConfigSection {
            public final ChameleonConfig.ConfigEntry<Boolean> enable;
            public final ChameleonConfig.ConfigEntry<Boolean> enableCycleRecipe;

            public FTBTeams (String name, String... comment) {
                super(name, comment);

                enable = commonConfig.define("enable", true)
                    .comment("Enables FTB Teams integration if mod is present.");

                enableCycleRecipe = commonConfig.define("enableCycleRecipe", true)
                    .comment("", "Enables recipe to obtain key from another supported personal key in crafting grid.");
            }

            @Override
            protected void buildEntries () {
                super.buildEntries();
                enable.build();
                enableCycleRecipe.build();
            }

            @Override
            public FTBTeams build () {
                super.build();
                return this;
            }
        }

        public Waila waila;
        public FTBChunks ftbChunks;
        public FTBTeams ftbTeams;

        public Integration () {
            commonConfig.comment("Configuration around integration with third party mods.");
            commonConfig.pushGroup("Integration");

            ftbChunks = new FTBChunks("FTBChunks",
                "Configuration around the FTB Chunks mod.",
                "Improves support for claimed chunks.").build();

            ftbTeams = new FTBTeams("FTBTeams",
                "Configuration around the FTB Teams mod.",
                "Adds support for a teams personal key.").build();

            waila = new Waila("WAILA",
                "Configuration around the WAILA/HWYLA/Jade family of block inspection mods.").build();

            commonConfig.popGroup();
        }
    }

    public enum Mode {
        NONE,
        LIST,
        ALL;

        public static Mode fromValueIgnoreCase (String value) {
            if (value.compareToIgnoreCase("NONE") == 0)
                return Mode.NONE;
            else if (value.compareToIgnoreCase("LIST") == 0)
                return Mode.LIST;
            else if (value.compareToIgnoreCase("ALL") == 0)
                return Mode.ALL;

            return LIST;
        }
    }

    public enum DropMode {
        KEEP,
        DROP,
        VOID;

        public static DropMode fromValueIgnoreCase (String value) {
            if (value.compareToIgnoreCase("KEEP") == 0)
                return DropMode.KEEP;
            else if (value.compareToIgnoreCase("DROP") == 0)
                return DropMode.DROP;
            else if (value.compareToIgnoreCase("VOID") == 0)
                return DropMode.VOID;

            return KEEP;
        }
    }
}
