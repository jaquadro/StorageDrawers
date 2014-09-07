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

    private static final String LANG_PREFIX = "storageDrawers.config.";

    private final Configuration config;

    public final List<ConfigSection> sections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionGeneral = new ConfigSection(sections, "general", "general");
    public final ConfigSection sectionBlocks = new ConfigSection(sections, "blocks", "blocks");
    public final ConfigSection sectionUpgrades = new ConfigSection(sections, "upgrades", "upgrades");

    public final List<ConfigSection> blockSections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionBlocksFullDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers2", "blocks.fullDrawers2");
    public final ConfigSection sectionBlocksFullDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "fulldrawers4", "blocks.fullDrawers4");
    public final ConfigSection sectionBlocksHalfDrawers1x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers2", "blocks.halfDrawers2");
    public final ConfigSection sectionBlocksHalfDrawers2x2 = new ConfigSection(blockSections, sectionBlocks, "halfdrawers4", "blocks.halfDrawers4");

    public Map<String, ConfigSection> blockSectionsMap = new HashMap<String, ConfigSection>();

    private Property itemRenderType;

    public ConfigManager (File file) {
        config = new Configuration(file);

        for (ConfigSection section : sections)
            section.getCategory();

        for (ConfigSection section : blockSections) {
            section.getCategory();
            blockSectionsMap.put(section.name, section);
        }

        syncConfig();
    }

    public void syncConfig () {
        itemRenderType = config.get(Configuration.CATEGORY_GENERAL, "itemRenderType", "fancy", null, new String[] { "fancy", "fast" });
        itemRenderType.setLanguageKey(LANG_PREFIX + "prop.itemRenderType");

        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "baseStorage", 8).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);

        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "baseStorage", 4).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);

        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "baseStorage", 4).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);

        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "baseStorage", 2).setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);

        if (config.hasChanged())
            config.save();
    }

    public boolean isFancyItemRenderEnabled () {
        if (itemRenderType.getString().equals("fancy"))
            return true;
        return false;
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
}
