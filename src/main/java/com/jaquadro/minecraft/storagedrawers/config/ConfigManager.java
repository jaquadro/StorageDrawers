package com.jaquadro.minecraft.storagedrawers.config;

import com.google.common.collect.Maps;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigManager
{
    public static final Map<UUID, Map<String, PlayerConfigSetting<?>>> serverPlayerConfigSettings = Maps.newHashMap();

    public class ConfigSection {
        public final ConfigSection parent;
        public final String name;
        public final String lang;

        private ConfigCategory category;

        public ConfigSection (List<ConfigSection> list, ConfigSection parent, String name, String lang) {
            this.parent = parent;
            this.name = name;
            this.lang = lang;

            list.add(this);
        }

        public ConfigSection (List<ConfigSection> list, String name, String lang) {
            this(list, null, name, lang);
        }

        public ConfigCategory getCategory () {
            if (category != null)
                return  category;

            if (parent != null)
                category = config.getCategory(parent.getCategory().getQualifiedName() + "." + name.toLowerCase());
            else
                category = config.getCategory(name.toLowerCase());

            category.setLanguageKey(LANG_PREFIX + lang);
            return category;
        }

        public String getQualifiedName () {
            return getCategory().getQualifiedName();
        }
    }

    public class ConfigCache {
        public boolean enableIndicatorUpgrades;
        public boolean enableStorageUpgrades;
        public boolean enableLockUpgrades;
        public boolean enableVoidUpgrades;
        public boolean enableCreativeUpgrades;
        public boolean enableShroudUpgrades;
        public boolean enableQuantifiableUpgrades;
        public boolean enablePersonalUpgrades;
        public boolean enableRedstoneUpgrades;
        public boolean renderStorageUpgrades;
        public boolean enableDrawerUI;
        public boolean creativeTabVanillaWoods;
        public boolean enableSidedInput;
        public boolean enableSidedOutput;
        public boolean enableItemConversion;
        public boolean enableWailaIntegration;
        public boolean enableTOPIntegration;
        public boolean enableThaumcraftIntegration;
        public boolean enableTape;
        public boolean enableFallbackRecipes;
        public boolean enableFramedDrawers;
        public boolean enableFramedTrims;
        public boolean enableFramingTable;
        public boolean consumeDecorationItems;
        public boolean invertShift;
        public boolean invertClick;
        public boolean debugTrace;
        public boolean stackRemainderWaila;
        public boolean registerExtraCompRules;
        public boolean defaultQuantify;
        public boolean keepContentsOnBreak;
        public String[] compRules;
        public String[] oreWhitelist;
        public String[] oreBlacklist;

        public int level2Mult;
        public int level3Mult;
        public int level4Mult;
        public int level5Mult;
        public int level6Mult;
    }

    private static final String LANG_PREFIX = "storagedrawers.config.";

    private final Configuration config;
    public final ConfigCache cache;

    public final List<ConfigSection> sections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionGeneral = new ConfigSection(sections, "general", "general");
    public final ConfigSection sectionIntegration = new ConfigSection(sections, "integration", "integration");
    public final ConfigSection sectionBlocks = new ConfigSection(sections, "blocks", "blocks");
    public final ConfigSection sectionUpgrades = new ConfigSection(sections, "upgrades", "upgrades");
    public final ConfigSection sectionRegistries = new ConfigSection(sections, "registries", "registries");

    public final List<ConfigSection> blockSections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionBlocksFullDrawers1x1 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers1", "blocks.fullDrawers1");
    public final ConfigSection sectionBlocksFullDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers2", "blocks.fullDrawers2");
    public final ConfigSection sectionBlocksFullDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers4", "blocks.fullDrawers4");
    public final ConfigSection sectionBlocksHalfDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers2", "blocks.halfDrawers2");
    public final ConfigSection sectionBlocksHalfDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers4", "blocks.halfDrawers4");
    public final ConfigSection sectionBlocksCompDrawers = new ConfigSection(blockSections, sectionBlocks, "compdrawers", "blocks.compDrawers");
    public final ConfigSection sectionBlocksController = new ConfigSection(blockSections, sectionBlocks, "controller", "blocks.controller");
    public final ConfigSection sectionBlocksTrim = new ConfigSection(blockSections, sectionBlocks, "trim", "blocks.trim");
    public final ConfigSection sectionBlocksSlave = new ConfigSection(blockSections, sectionBlocks, "controllerslave", "blocks.controllerSlave");
    public final ConfigSection sectionFramedBlocks = new ConfigSection(blockSections, sectionBlocks, "framedblocks", "blocks.framedBlocks");

    public Map<String, ConfigSection> blockSectionsMap = new HashMap<String, ConfigSection>();

    public ConfigManager (File file) {
        config = new Configuration(file);
        cache = new ConfigCache();

        for (ConfigSection section : sections)
            section.getCategory();

        for (ConfigSection section : blockSections) {
            section.getCategory();
            blockSectionsMap.put(section.name, section);
        }

        syncConfig();
    }

    public void syncConfig () {
        cache.enableIndicatorUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableIndicatorUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableIndicatorUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableStorageUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableStorageUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableStorageUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableLockUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableLockUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableLockUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableVoidUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableVoidUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableVoidUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableCreativeUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableCreativeUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableCreativeUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableShroudUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableShroudUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableShroudUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableQuantifiableUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableQuantifiableUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableQuantifiableUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enablePersonalUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enablePersonalUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enablePersonalUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableRedstoneUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableRedstoneUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.enableRedstoneUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableTape = config.get(Configuration.CATEGORY_GENERAL, "enableTape", true).setLanguageKey(LANG_PREFIX + "prop.enableTape").setRequiresMcRestart(true).getBoolean();
        cache.creativeTabVanillaWoods = config.get(Configuration.CATEGORY_GENERAL, "creativeTabVanillaWoods", true).setLanguageKey(LANG_PREFIX + "prop.creativeTabVanillaWoods").getBoolean();
        cache.enableDrawerUI = config.get(Configuration.CATEGORY_GENERAL, "enableDrawerUI", true).setLanguageKey(LANG_PREFIX + "prop.enableDrawerUI").getBoolean();
        cache.enableSidedInput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedInput", true).setLanguageKey(LANG_PREFIX + "prop.enableSidedInput").getBoolean();
        cache.enableSidedOutput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedOutput", true).setLanguageKey(LANG_PREFIX + "prop.enableSidedOutput").getBoolean();
        cache.enableItemConversion = config.get(Configuration.CATEGORY_GENERAL, "enableItemConversion", true).setLanguageKey(LANG_PREFIX + "prop.enableItemConversion").getBoolean();
        cache.enableFallbackRecipes = config.get(Configuration.CATEGORY_GENERAL, "enableFallbackRecipes", true).setLanguageKey(LANG_PREFIX + "prop.enableFallbackRecipes").setRequiresMcRestart(true).getBoolean();
        cache.stackRemainderWaila = !config.get(Configuration.CATEGORY_GENERAL, "wailaStackRemainder", "stack + remainder", null, new String[]{"exact", "stack + remainder"}).setLanguageKey(LANG_PREFIX + "prop.wailaStackRemainder").getString().equals("exact");
        cache.invertShift = config.get(Configuration.CATEGORY_GENERAL, "invertShift", false,
            "Inverts how shift works with drawers. If this is true, shifting will only give one item, where regular clicks will give a full stack. Leave false for default behavior.")
            .setLanguageKey(LANG_PREFIX + "prop.invertShift").getBoolean();
        cache.invertClick = config.get(Configuration.CATEGORY_GENERAL, "invertClick", false,
            "Inverts left and right click action on drawers.  If this is true, left click will insert items and right click will extract items.  Leave false for default behavior.")
            .setLanguageKey(LANG_PREFIX + "prop.invertClick").getBoolean();
        cache.debugTrace = config.get(Configuration.CATEGORY_GENERAL, "enableDebugLogging", false,
            "Writes additional log messages while using the mod.  Mainly for debug purposes.  Should be kept disabled unless instructed otherwise.")
            .setLanguageKey(LANG_PREFIX + "prop.enableDebugLogging").getBoolean();
        cache.defaultQuantify = config.get(Configuration.CATEGORY_GENERAL, "defaultQuantify", false).setLanguageKey(LANG_PREFIX + "prop.defaultQuantify").getBoolean();
        cache.keepContentsOnBreak = config.get(Configuration.CATEGORY_GENERAL, "keepContentsOnBreak", true).setLanguageKey(LANG_PREFIX + "prop.keepContentsOnBreak").getBoolean();

        //cache.enableAE2Integration = config.get(sectionIntegration.getQualifiedName(), "enableAE2", true).setLanguageKey(LANG_PREFIX + "integration.enableAE2").setRequiresMcRestart(true).getBoolean();
        cache.enableWailaIntegration = config.get(sectionIntegration.getQualifiedName(), "enableWaila", true,
                "Whether to enable What Am I Looking At integration, which overrides the displayed block for Storage Drawers related blocks, and adds several Storage Drawers related options to the config. Warning: Turning this off will make Waila display some Storage Drawers blocks incorrectly.")
                .setLanguageKey(LANG_PREFIX + "integration.enableWaila").setRequiresMcRestart(true).getBoolean();
        cache.enableTOPIntegration = config.get(sectionIntegration.getQualifiedName(), "enableTOP", true,
                "Whether to enable The One Probe integration, which overrides the displayed block for Storage Drawers related blocks. Warning: Turning this off will make TOP display some Storage Drawers blocks incorrectly.")
                .setLanguageKey(LANG_PREFIX + "integration.enableTOP").setRequiresMcRestart(true).getBoolean();
        cache.enableThaumcraftIntegration = config.get(sectionIntegration.getQualifiedName(), "enableThaumcraft", true,
                "Whether to enable Thaumcraft integration, which adds icons on drawers if the item stored has an Aspect.")
                .setLanguageKey(LANG_PREFIX + "integration.enableThaumcraft").setRequiresMcRestart(true).getBoolean();

        cache.compRules = config.getStringList("compactingRules", sectionRegistries.getQualifiedName(), new String[] { "minecraft:clay, minecraft:clay_ball, 4" }, "Items should be in form domain:item or domain:item:meta.", null, LANG_PREFIX + "registries.compRules");
        if (StorageDrawers.compRegistry != null) {
            for (String rule : cache.compRules)
                StorageDrawers.compRegistry.register(rule);
        }

        cache.oreBlacklist = config.getStringList("oreBlacklist", sectionRegistries.getQualifiedName(), new String[0], "List of ore dictionary names to blacklist for substitution.", null, LANG_PREFIX + "registries.oreBlacklist");
        if (StorageDrawers.oreDictRegistry != null) {
            for (String item : cache.oreBlacklist) {
                StorageDrawers.oreDictRegistry.removeWhitelist(item);
                StorageDrawers.oreDictRegistry.addBlacklist(item);
            }
        }

        cache.oreWhitelist = config.getStringList("oreWhitelist", sectionRegistries.getQualifiedName(), new String[0], "List of ore dictionary names to whitelist for substitution.", null, LANG_PREFIX + "registries.oreWhitelist");
        if (StorageDrawers.oreDictRegistry != null) {
            for (String item : cache.oreWhitelist) {
                StorageDrawers.oreDictRegistry.removeBlacklist(item);
                StorageDrawers.oreDictRegistry.addWhitelist(item);
            }
        }

        cache.registerExtraCompRules = config.get(sectionRegistries.getQualifiedName(), "registerExtraCompactingRules", true).setLanguageKey(LANG_PREFIX + "registries.registerExtraCompRules").setRequiresWorldRestart(true).getBoolean();

        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "baseStorage", 32).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "recipeOutput", 1).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "baseStorage", 16).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "recipeOutput", 2).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "baseStorage", 8).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "recipeOutput", 4).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "baseStorage", 8).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "recipeOutput", 2).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "baseStorage", 4).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "recipeOutput", 4).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksCompDrawers.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "baseStorage", 16).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "recipeOutput", 1).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksController.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksController.getQualifiedName(), "range", 12).setLanguageKey(LANG_PREFIX + "prop.controllerRange");
        config.get(sectionBlocksController.getQualifiedName(), "enableControllerIO", true).setLanguageKey(LANG_PREFIX + "prop.enableControllerIO").getBoolean();

        config.get(sectionBlocksTrim.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksTrim.getQualifiedName(), "recipeOutput", 4).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksSlave.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);

        cache.enableFramedDrawers = config.get(sectionFramedBlocks.getQualifiedName(), "enableFramedDrawers", true).setLanguageKey(LANG_PREFIX + "framedBlocks.enableFramedDrawers").setRequiresMcRestart(true).getBoolean();
        cache.enableFramedTrims = config.get(sectionFramedBlocks.getQualifiedName(), "enableFramedTrims", true).setLanguageKey(LANG_PREFIX + "framedBlocks.enableFramedTrims").setRequiresMcRestart(true).getBoolean();
        cache.enableFramingTable = config.get(sectionFramedBlocks.getQualifiedName(), "enableFramingTable", true).setLanguageKey(LANG_PREFIX + "framedBlocks.enableFramingTable").setRequiresMcRestart(true).getBoolean();
        cache.consumeDecorationItems = config.get(sectionFramedBlocks.getQualifiedName(), "consumeDecorationItems", true,
                "Changes whether items used for decoration in the Framing Table gets consumed. Leave true to consume items (default behaviour).")
                .setLanguageKey(LANG_PREFIX + "framedBlocks.consumeDecorationItems").getBoolean();

        cache.level2Mult = config.get(sectionUpgrades.getQualifiedName(), "level2Mult", 2).setLanguageKey(LANG_PREFIX + "upgrades.level2Mult").setRequiresWorldRestart(true).getInt();
        cache.level3Mult = config.get(sectionUpgrades.getQualifiedName(), "level3Mult", 4).setLanguageKey(LANG_PREFIX + "upgrades.level3Mult").setRequiresWorldRestart(true).getInt();
        cache.level4Mult = config.get(sectionUpgrades.getQualifiedName(), "level4Mult", 8).setLanguageKey(LANG_PREFIX + "upgrades.level4Mult").setRequiresWorldRestart(true).getInt();
        cache.level5Mult = config.get(sectionUpgrades.getQualifiedName(), "level5Mult", 16).setLanguageKey(LANG_PREFIX + "upgrades.level5Mult").setRequiresWorldRestart(true).getInt();
        cache.level6Mult = config.get(sectionUpgrades.getQualifiedName(), "level6Mult", 32).setLanguageKey(LANG_PREFIX + "upgrades.level6Mult").setRequiresWorldRestart(true).getInt();

        getControllerRange();

        if (config.hasChanged())
            config.save();
    }

    public String getPath () {
        return config.toString();
    }

    public boolean isBlockEnabled (String blockName) {
        blockName = blockName.toLowerCase();
        if (!blockSectionsMap.containsKey(blockName))
            return false;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("enabled").getBoolean();
    }

    public int getBlockBaseStorage (String blockName) {
        if (!blockSectionsMap.containsKey(blockName))
            return 0;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("baseStorage").getInt();
    }

    public int getBlockRecipeOutput (String blockName) {
        if (!blockSectionsMap.containsKey(blockName))
            return 0;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("recipeOutput").getInt();
    }

    public int getControllerRange () {
        ConfigSection section = blockSectionsMap.get("controller");
        return section.getCategory().get("range").getInt();
    }

    public boolean allowControllerIO() {
        ConfigSection section = blockSectionsMap.get("controller");
        return section.getCategory().get("enableControllerIO").getBoolean();
    }

    public int getStorageUpgradeMultiplier (int level) {
        return switch (level) {
            case 2 -> cache.level2Mult;
            case 3 -> cache.level3Mult;
            case 4 -> cache.level4Mult;
            case 5 -> cache.level5Mult;
            case 6 -> cache.level6Mult;
            default -> 1;
        };
    }
}
