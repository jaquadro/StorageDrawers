package com.jaquadro.minecraft.chameleon.core;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    public void initSidedResources () {
        Chameleon.instance.iconRegistry = new IconRegistry();
    }

    @SubscribeEvent
    public void onPreTextureStitch (TextureStitchEvent.Pre event) {
        Chameleon.instance.iconRegistry.loadIcons(event.map);
    }
}
