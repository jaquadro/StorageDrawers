package com.jaquadro.minecraft.storagedrawers.packs.bop;

import com.jaquadro.minecraft.storagedrawers.packs.bop.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.packs.bop.core.ModRecipes;
import com.jaquadro.minecraft.storagedrawers.packs.bop.core.RefinedRelocation;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = StorageDrawersPack.MOD_ID, name = StorageDrawersPack.MOD_NAME, version = StorageDrawersPack.MOD_VERSION, dependencies = "required-after:StorageDrawers;")
public class StorageDrawersPack
{
    public static final String MOD_ID = "StorageDrawersBop";
    public static final String MOD_NAME = "Storage Drawers: Biomes O' Plenty Pack";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.packs.bop.";

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
