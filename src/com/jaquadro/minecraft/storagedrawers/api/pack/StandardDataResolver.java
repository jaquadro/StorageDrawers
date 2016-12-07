package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class StandardDataResolver implements IPackDataResolver
{
    private String modID;
    private String[] unlocalizedNames;
    private CreativeTabs creativeTab;

    private Block[] planks = new Block[16];
    private int[] planksMeta = new int[16];

    private Block[] slabs = new Block[16];
    private int[] slabsMeta = new int [16];

    public StandardDataResolver (String modID, String[] unlocalizedNames) {
        this.modID = modID;
        this.unlocalizedNames = unlocalizedNames;
    }

    public StandardDataResolver (String modID, String[] unlocalizedNames, CreativeTabs creativeTab) {
        this(modID, unlocalizedNames);
        this.creativeTab = creativeTab;
    }

    @Override
    public String getPackModID () {
        return modID;
    }

    protected String makeBlockName (String name) {
        return getPackModID().toLowerCase() + "." + name;
    }

    @Override
    public String getBlockName (BlockConfiguration blockConfig) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
                if (blockConfig.getDrawerCount() == 1)
                    return makeBlockName("fullDrawers1");
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return makeBlockName("fullDrawers2");
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return makeBlockName("fullDrawers4");
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return makeBlockName("halfDrawers2");
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return makeBlockName("halfDrawers4");
                break;
            case Trim:
                return makeBlockName("trim");
        }
        return null;
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return creativeTab;
    }

    @Override
    public boolean isValidMetaValue (int meta) {
        if (meta < 0 || meta >= unlocalizedNames.length)
            return false;

        return unlocalizedNames != null && unlocalizedNames[meta] != null;
    }

    @Override
    public String getUnlocalizedName (int meta) {
        if (!isValidMetaValue(meta))
            return null;

        return unlocalizedNames[meta];
    }

    protected String getBaseTexturePath () {
        return getPackModID() + ":";
    }

    protected String getTextureMetaName (int meta) {
        return getUnlocalizedName(meta);
    }

    @Override
    public String getTexturePath (TextureType type, int meta) {
        switch (type) {
            case Front1:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_1";
            case Front2:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_2";
            case Front4:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_4";
            case Side:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side";
            case SideVSplit:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side_v";
            case SideHSplit:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side_h";
            case TrimBorder:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_trim";
            case TrimBlock:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side";
            default:
                return "";
        }
    }

    @Override
    public Block getBlock (BlockConfiguration blockConfig) {
        return null;
    }

    @Override
    public Block getPlankBlock (int meta) {
        return planks[meta];
    }

    @Override
    public Block getSlabBlock (int meta) {
        return slabs[meta];
    }

    @Override
    public int getPlankMeta (int meta) {
        return planksMeta[meta];
    }

    @Override
    public int getSlabMeta (int meta) {
        return slabsMeta[meta];
    }

    public void init () {

    }

    protected void setPlankSlab (int meta, Block plank, int plankMeta, Block slab, int slabMeta) {
        if (plank != null) {
            planks[meta] = plank;
            planksMeta[meta] = plankMeta;
        }

        if (slab != null) {
            slabs[meta] = slab;
            slabsMeta[meta] = slabMeta;
        }
    }
}
