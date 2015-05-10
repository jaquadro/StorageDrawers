package com.jaquadro.minecraft.chameleon;

import com.jaquadro.minecraft.chameleon.core.CommonProxy;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Chameleon.MOD_ID, name = Chameleon.MOD_NAME, version = Chameleon.MOD_VERSION)
public class Chameleon
{
    public static final String MOD_ID = "Chameleon";
    public static final String MOD_NAME = "Chameleon";
    public static final String MOD_VERSION = "@VERSION@";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.chameleon.";

    @SideOnly(Side.CLIENT)
    public IconRegistry iconRegistry;

    @Mod.Instance(MOD_ID)
    public static Chameleon instance;

    @SidedProxy(clientSide = SOURCE_PATH + "core.ClientProxy", serverSide = SOURCE_PATH + "core.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        proxy.initSidedResources();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(proxy);
    }
}
