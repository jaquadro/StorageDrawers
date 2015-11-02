package com.jaquadro.minecraft.storagedrawers.packs.forestry;

import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.forestry.core.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = StorageDrawersPack.MOD_ID, name = StorageDrawersPack.MOD_NAME, version = StorageDrawersPack.MOD_VERSION, dependencies = "required-after:StorageDrawers;")
public class StorageDrawersPack
{
    public static final String MOD_ID = "StorageDrawersForestry";
    public static final String MOD_NAME = "Storage Drawers: Forestry Pack";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.packs.forestry.";

    public IPackDataResolver resolver1 = new DataResolver(MOD_ID, new String[] {
        "larch", "teak", "acacia", "lime", "chestnut", "wenge", "baobab", "sequoia", "kapok", "ebony", "mahogany", "balsa", "willow", "walnut", "greenheart", "cherry"
    });

    public IPackDataResolver resolver2 = new DataResolver(MOD_ID, new String[] {
        "mahoe", "poplar", "palm", "papaya", "pine", "plum", "maple", "citrus", "giganteum", "ipe", "padauk", "cocobolo", "zebrawood"
    });

    public ModBlocks blocks = new ModBlocks();
    public ModRecipes recipes = new ModRecipes();

    @Mod.Instance(MOD_ID)
    public static StorageDrawersPack instance;

    @SidedProxy(clientSide = SOURCE_PATH + "CommonProxy", serverSide = SOURCE_PATH + "CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        blocks.init();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        RefinedRelocation.init();
        recipes.init();
    }

    @Mod.EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        RefinedRelocation.postInit();
    }
}
