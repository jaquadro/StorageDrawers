package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager
{
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
        public boolean renderStorageUpgrades;
        public String itemRenderType;
        public boolean creativeTabVanillaWoods;
        public boolean enableSidedInput;
        public boolean enableSidedOutput;
        public boolean enableWailaIntegration;
        public boolean enableAE2Integration;

        public int level2Mult;
        public int level3Mult;
        public int level4Mult;
        public int level5Mult;
        public int level6Mult;
    }

    private static final String LANG_PREFIX = "storageDrawers.config.";

    private final Configuration config;
    public final ConfigCache cache;

    public final List<ConfigSection> sections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionGeneral = new ConfigSection(sections, "general", "general");
    public final ConfigSection sectionIntegration = new ConfigSection(sections, "integration", "integration");
    public final ConfigSection sectionBlocks = new ConfigSection(sections, "blocks", "blocks");
    public final ConfigSection sectionUpgrades = new ConfigSection(sections, "upgrades", "upgrades");

    public final List<ConfigSection> blockSections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionBlocksFullDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers2", "blocks.fullDrawers2");
    public final ConfigSection sectionBlocksFullDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers4", "blocks.fullDrawers4");
    public final ConfigSection sectionBlocksHalfDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers2", "blocks.halfDrawers2");
    public final ConfigSection sectionBlocksHalfDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers4", "blocks.halfDrawers4");
    public final ConfigSection sectionBlocksCompDrawers = new ConfigSection(blockSections, sectionBlocks, "compdrawers", "blocks.compDrawers");

    public Map<String, ConfigSection> blockSectionsMap = new HashMap<String, ConfigSection>();

    //private Property itemRenderType;



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
        cache.itemRenderType = config.get(Configuration.CATEGORY_GENERAL, "itemRenderType", "fast", null, new String[] { "fancy", "fast" }).setLanguageKey(LANG_PREFIX + "prop.itemRenderType").getString();
        cache.renderStorageUpgrades = config.get(Configuration.CATEGORY_GENERAL, "renderStorageUpgrades", true).setLanguageKey(LANG_PREFIX + "prop.renderStorageUpgrades").getBoolean();
        cache.creativeTabVanillaWoods = config.get(Configuration.CATEGORY_GENERAL, "creativeTabVanillaWoods", true).setLanguageKey(LANG_PREFIX + "prop.creativeTabVanillaWoods").getBoolean();
        cache.enableSidedInput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedInput", true).setLanguageKey(LANG_PREFIX + "prop.enableSidedInput").getBoolean();
        cache.enableSidedOutput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedOutput", true).setLanguageKey(LANG_PREFIX + "prop.enableSidedOutput").getBoolean();

        cache.enableAE2Integration = config.get(sectionIntegration.getQualifiedName(), "enableAE2", true).setLanguageKey(LANG_PREFIX + "integration.enableAE2").setRequiresMcRestart(true).getBoolean();
        cache.enableWailaIntegration = config.get(sectionIntegration.getQualifiedName(), "enableWaila", true).setLanguageKey(LANG_PREFIX + "integration.enableWaila").setRequiresMcRestart(true).getBoolean();

        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "baseStorage", 8).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "recipeOutput", 2).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "baseStorage", 4).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "recipeOutput", 4).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "baseStorage", 4).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "recipeOutput", 2).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "baseStorage", 2).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "recipeOutput", 4).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksCompDrawers.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "baseStorage", 8).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "recipeOutput", 1).setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        cache.level2Mult = config.get(sectionUpgrades.getQualifiedName(), "level2Mult", 2).setLanguageKey(LANG_PREFIX + "upgrades.level2Mult").setRequiresWorldRestart(true).getInt();
        cache.level3Mult = config.get(sectionUpgrades.getQualifiedName(), "level3Mult", 3).setLanguageKey(LANG_PREFIX + "upgrades.level3Mult").setRequiresWorldRestart(true).getInt();
        cache.level4Mult = config.get(sectionUpgrades.getQualifiedName(), "level4Mult", 5).setLanguageKey(LANG_PREFIX + "upgrades.level4Mult").setRequiresWorldRestart(true).getInt();
        cache.level5Mult = config.get(sectionUpgrades.getQualifiedName(), "level5Mult", 8).setLanguageKey(LANG_PREFIX + "upgrades.level5Mult").setRequiresWorldRestart(true).getInt();
        cache.level6Mult = config.get(sectionUpgrades.getQualifiedName(), "level6Mult", 13).setLanguageKey(LANG_PREFIX + "upgrades.level6Mult").setRequiresWorldRestart(true).getInt();

        if (config.hasChanged())
            config.save();
    }

    public boolean isFancyItemRenderEnabled () {
        return cache.itemRenderType.equals("fancy");
    }

    public String getPath () {
        return config.toString();
    }

    public boolean isBlockEnabled (String blockName) {
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

    public int getStorageUpgradeMultiplier (int level) {
        switch (level) {
            case 2: return cache.level2Mult;
            case 3: return cache.level3Mult;
            case 4: return cache.level4Mult;
            case 5: return cache.level5Mult;
            case 6: return cache.level6Mult;
            default: return 1;
        }
    }
}
