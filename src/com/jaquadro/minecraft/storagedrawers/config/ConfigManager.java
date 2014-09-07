package com.jaquadro.minecraft.storagedrawers.config;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
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

    public Map<String, ConfigCategory> blockSubcategories;

    private Property itemRenderType;

    public ConfigManager (File file) {
        config = new Configuration(file);

        for (ConfigSection section : sections)
            section.getCategory();

        for (ConfigSection section : blockSections)
            section.getCategory();

        syncConfig();
    }

    public void syncConfig () {
        itemRenderType = config.get(Configuration.CATEGORY_GENERAL, "Item Render Type", "fancy", null, new String[] { "fancy", "fast" });

        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "enabled", true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "baseStorage", 8);

        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "enabled", true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "baseStorage", 4);

        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "enabled", true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "baseStorage", 4);

        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "enabled", true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "baseStorage", 2);

        if (config.hasChanged())
            config.save();
    }

    public boolean fancyItemRenderEnabled () {
        if (itemRenderType.getString().equals("fancy"))
            return true;
        return false;
    }

    public String getPath () {
        return config.toString();
    }

    private String getBlockName (Block block) {
        return block.getUnlocalizedName().replace("tile.", "");
    }
}
